package me.melontini.commander.impl.expression;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.FunctionDictionaryIfc;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.functions.*;
import me.melontini.commander.impl.expression.functions.arrays.*;
import me.melontini.commander.impl.expression.functions.math.ClampFunction;
import me.melontini.commander.impl.expression.functions.math.LerpFunction;
import me.melontini.commander.impl.expression.functions.math.RangedRandomFunction;
import me.melontini.commander.impl.mixin.evalex.EvaluationValueAccessor;
import me.melontini.commander.impl.mixin.evalex.ExpressionAccessor;
import me.melontini.commander.impl.mixin.evalex.ExpressionConfigurationAccessor;
import me.melontini.commander.impl.mixin.evalex.MapBasedFunctionDictionaryAccessor;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EvalUtils {

    public static final ExpressionConfiguration CONFIGURATION;
    public static final EvaluationValue TRUE = EvaluationValueAccessor.commander$init(true, EvaluationValue.DataType.BOOLEAN);
    public static final EvaluationValue FALSE = EvaluationValueAccessor.commander$init(false, EvaluationValue.DataType.BOOLEAN);
    public static final EvaluationValue NULL = EvaluationValueAccessor.commander$init(null, EvaluationValue.DataType.NULL);

    static {
        var builder = ExpressionConfiguration.builder()
                .dataAccessorSupplier(LootContextDataAccessor::new)
                .evaluationValueConverter(new ReflectiveValueConverter())
                .allowOverwriteConstants(false)
                .singleQuoteStringLiteralsAllowed(true);

        var fd = ExpressionConfiguration.defaultConfiguration().getFunctionDictionary();
        Map<String, FunctionIfc> functions = new HashMap<>(((MapBasedFunctionDictionaryAccessor) fd)
                .commander$getFunctions().entrySet().stream()
                .collect(Collectors.toMap(e -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.getKey()), Map.Entry::getValue)));
        functions.put("random", new RangedRandomFunction());
        functions.put("lerp", new LerpFunction());
        functions.put("clamp", new ClampFunction());
        functions.put("ifMatches", new MatchesFunction());
        functions.put("length", new LengthFunction());

        functions.put("arrayOf", new ArrayOf());
        functions.put("arrayMap", new ArrayMap());
        functions.put("arrayFind", new ArrayFind());
        functions.put("arrayAnyMatch", new ArrayAnyMatch());
        functions.put("arrayNoneMatch", new ArrayNoneMatch());
        functions.put("arrayAllMatch", new ArrayAllMatch());

        functions.put("strFormat", new StringFormatFunction());

        functions.put("structContainsKey", new StructContainsKeyFunction());
        functions.put("hasContext", new HasContextFunction());
        builder.functionDictionary(SimpleFunctionDictionary.ofFunctions(functions));

        CONFIGURATION = builder.build();
        ((ExpressionConfigurationAccessor) CONFIGURATION).commander$defaultConstants(ImmutableMap.of(
                "true", TRUE,
                "false", FALSE,
                "PI", EvaluationValue.numberValue(new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")),
                "E", EvaluationValue.numberValue(new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663")),
                "null", NULL,
                "DT_FORMAT_ISO_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]['['VV']']"),
                "DT_FORMAT_LOCAL_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS]"),
                "DT_FORMAT_LOCAL_DATE", EvaluationValue.stringValue("yyyy-MM-dd")
        ));
    }

    @SneakyThrows
    public static EvaluationValue runLambda(Expression expression, EvaluationValue value, ASTNode predicate) {
        try {
            return expression.with("it", value).evaluateSubtree(predicate);
        } finally {
            expression.getDataAccessor().setData("it", null);
        }
    }

    public static EvaluationValue evaluate(LootContext context, Expression exp) {
        try {
            LootContextDataAccessor.LOCAL.set(context);
            return exp.evaluate();
        } catch (EvaluationException | ParseException e) {
            throw new CmdEvalException(Objects.requireNonNullElseGet(e.getMessage(), () -> "Failed to evaluate expression %s".formatted(exp.getExpressionString())), e);
        } finally {
            LootContextDataAccessor.LOCAL.remove();
        }
    }

    public static DataResult<Expression> parseExpression(String expression) {
        try {
            Expression exp = new Expression(expression, CONFIGURATION);
            ((ExpressionAccessor) exp).commander$constants(new HashMap<>(CONFIGURATION.getDefaultConstants()));
            exp.validate();
            return DataResult.success(exp);
        } catch (Throwable throwable) {
            return DataResult.error(throwable::getLocalizedMessage);
        }
    }

    public static class LootContextDataAccessor implements DataAccessorIfc {

        private static final Map<Identifier, Function<LootContext, Object>> overrides = ImmutableMap.of(
                new Identifier("level"), LootContext::getWorld,
                new Identifier("luck"), LootContext::getLuck
        );
        public static final ThreadLocal<LootContext> LOCAL = new ThreadLocal<>();
        private final Map<String, EvaluationValue> parameters = new HashMap<>();

        @Override
        public @Nullable EvaluationValue getData(String variable) {
            var localParam = parameters.get(variable);
            if (localParam != null) return localParam;

            var r = Identifier.validate(variable);
            if (r.error().isPresent()) {
                throw new CmdEvalException("%s - %s".formatted(variable, r.error().orElseThrow().message()));
            }

            var id = r.result().orElseThrow();
            var func = overrides.get(id);
            if (func != null) return CONFIGURATION.getEvaluationValueConverter().convertObject(func.apply(LOCAL.get()), CONFIGURATION);

            var param = ExtractionTypes.getParameter(id);
            if (param == null) throw new CmdEvalException("%s is not a registered loot context parameter!".formatted(id));

            var object = LOCAL.get().get(param);
            if (object == null) return null;
            return CONFIGURATION.getEvaluationValueConverter().convertObject(object, CONFIGURATION);
        }

        @Override
        public void setData(String variable, EvaluationValue value) {
            parameters.put(variable, value);
        }
    }

    public static class SimpleFunctionDictionary implements FunctionDictionaryIfc {

        private final Map<String, FunctionIfc> functions = new Object2ObjectOpenHashMap<>();

        public static FunctionDictionaryIfc ofFunctions(Map<String, FunctionIfc> functions) {
            FunctionDictionaryIfc dictionary = new SimpleFunctionDictionary();
            functions.forEach(dictionary::addFunction);
            return dictionary;
        }

        @Override
        public @Nullable FunctionIfc getFunction(String functionName) {
            return functions.get(functionName);
        }

        @Override
        public void addFunction(String functionName, FunctionIfc function) {
            functions.put(functionName, function);
        }
    }

    public static void init() {
    }
}
