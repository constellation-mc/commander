package me.melontini.commander.impl.expression;

import com.ezylang.evalex.BaseException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.FunctionDictionaryIfc;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.parser.ASTNode;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.functions.*;
import me.melontini.commander.impl.expression.functions.arrays.*;
import me.melontini.commander.impl.expression.functions.math.ClampFunction;
import me.melontini.commander.impl.expression.functions.math.LerpFunction;
import me.melontini.commander.impl.expression.functions.math.RangedRandomFunction;
import me.melontini.commander.impl.expression.functions.registry.DynamicRegistryFunction;
import me.melontini.commander.impl.expression.functions.registry.DynamicRegistryRegistryFunction;
import me.melontini.commander.impl.expression.functions.registry.RegistryFunction;
import me.melontini.commander.impl.mixin.evalex.ExpressionConfigurationAccessor;
import me.melontini.commander.impl.mixin.evalex.MapBasedFunctionDictionaryAccessor;
import me.melontini.commander.impl.util.ASTInliner;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.functions.Memoize;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j2
public class EvalUtils {

    public static final ExpressionConfiguration CONFIGURATION;
    public static final Object2ReferenceMap<String, EvaluationValue> CONSTANTS = Object2ReferenceMaps.unmodifiable(new Object2ReferenceOpenHashMap<>(ImmutableMap.of(
            "true", EvaluationValue.TRUE,
            "false", EvaluationValue.FALSE,
            "PI", EvaluationValue.numberValue(new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")),
            "E", EvaluationValue.numberValue(new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663")),
            "null", EvaluationValue.NULL_VALUE,
            "DT_FORMAT_ISO_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]['['VV']']"),
            "DT_FORMAT_LOCAL_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS]"),
            "DT_FORMAT_LOCAL_DATE", EvaluationValue.stringValue("yyyy-MM-dd")
    )));

    static {
        var builder = ExpressionConfiguration.builder()
                .dataAccessorSupplier(LootContextDataAccessor::new)
                .evaluationValueConverter(new ReflectiveValueConverter())
                .allowOverwriteConstants(false)
                .singleQuoteStringLiteralsAllowed(true);

        var fd = ExpressionConfiguration.defaultConfiguration().getFunctionDictionary();
        Map<String, FunctionIfc> functions = new Object2ReferenceOpenHashMap<>(((MapBasedFunctionDictionaryAccessor) fd)
                .commander$getFunctions().entrySet().stream()
                .collect(Collectors.toMap(e -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.getKey()), Map.Entry::getValue)));

        Set<String> toCache = ImmutableSet.of(
                "fact", "sqrt",
                "acos", "acosh", "acosr", "acot", "acoth", "acotr", "asin", "asinh", "asinr",
                "atan", "atan2", "atan2r", "atanh", "atanr", "cosh", "cosr", "cot", "coth", "cotr",
                "csc", "csch", "cscr", "sinh", "sinr", "sec", "sech", "secr", "tanh", "tanr",
                "strContains", "strEndsWith", "strLower", "strStartsWith", "strTrim", "strUpper"
        );
        toCache.forEach(f -> functions.put(f, LruCachingFunction.of(functions.get(f))));

        functions.put("random", new RangedRandomFunction());
        functions.put("lerp", new LerpFunction());
        functions.put("clamp", new ClampFunction());
        functions.put("ifMatches", new MatchesFunction());
        functions.put("chain", new ChainFunction());
        functions.put("length", new LengthFunction());

        functions.put("arrayOf", new ArrayOf());
        functions.put("arrayMap", new ArrayMap());
        functions.put("arrayFind", new ArrayFind());
        functions.put("arrayFindAny", new ArrayFindAny());
        functions.put("arrayFindFirst", new ArrayFindFirst());
        functions.put("arrayAnyMatch", new ArrayAnyMatch());
        functions.put("arrayNoneMatch", new ArrayNoneMatch());
        functions.put("arrayAllMatch", new ArrayAllMatch());

        functions.put("structContainsKey", new StructContainsKeyFunction());
        functions.put("hasContext", new HasContextFunction());

        functions.put("Registry", new RegistryFunction(Registries.REGISTRIES));
        functions.put("Item", LruCachingFunction.of(new RegistryFunction(Registries.ITEM)));
        functions.put("Block", LruCachingFunction.of(new RegistryFunction(Registries.BLOCK)));

        functions.put("DynamicRegistry", new DynamicRegistryRegistryFunction());
        functions.put("Biome", new DynamicRegistryFunction(RegistryKeys.BIOME));
        functions.put("DimensionType", new DynamicRegistryFunction(RegistryKeys.DIMENSION_TYPE));
        builder.functionDictionary(SimpleFunctionDictionary.ofFunctions(functions));

        CONFIGURATION = builder.build();
        ((ExpressionConfigurationAccessor) CONFIGURATION).commander$defaultConstants(CONSTANTS);
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
        } catch (BaseException e) {
            throw new CmdEvalException(Objects.requireNonNullElseGet(e.getMessage(), () -> "Failed to evaluate expression %s".formatted(exp.getExpressionString())), e);
        } finally {
            LootContextDataAccessor.LOCAL.remove();
        }
    }

    private static final Object CACHE_LOCK = new Object();
    private static Function<String, Expression> EXPRESSION_CACHE;

    static {
        resetCache();
    }

    public static void resetCache() {
        synchronized (CACHE_LOCK) {
            EXPRESSION_CACHE = Memoize.lruFunction(Exceptions.function(key -> {
                Expression exp = new Expression(key, CONFIGURATION);
                ASTInliner.optimize(exp, exp.getAbstractSyntaxTree());
                return exp;
            }), 60);
        }
    }

    public static DataResult<Expression> parseExpression(String expression) {
        try {
            Expression result;
            synchronized (CACHE_LOCK) {
                result = EXPRESSION_CACHE.apply(expression);
            }
            return DataResult.success(result.copy());
        } catch (Throwable throwable) {
            var unwrapped = Exceptions.unwrap(throwable);
            return DataResult.error(unwrapped::getMessage);
        }
    }

    public static class LootContextDataAccessor implements DataAccessorIfc {

        private static final Map<Identifier, Function<LootContext, Object>> overrides = new Object2ReferenceOpenHashMap<>(Map.of(
                new Identifier("level"), LootContext::getWorld,
                new Identifier("luck"), LootContext::getLuck
        ));
        public static final ThreadLocal<LootContext> LOCAL = new ThreadLocal<>();
        private final Map<String, EvaluationValue> parameters = new Object2ReferenceOpenHashMap<>();
        //In most cases the expression is reused, so caching this helps us avoid some big overhead.
        private final Map<String, Supplier<EvaluationValue>> varCache = new Object2ReferenceOpenHashMap<>();

        @Override
        public @Nullable EvaluationValue getData(String variable) {
            var supplier = varCache.get(variable);
            if (supplier != null) return supplier.get(); //Parameters are cached by setData, so this is fine.

            var r = Identifier.validate(variable);
            if (r.error().isPresent()) {
                throw new CmdEvalException("%s - no such variable or %s".formatted(variable, r.error().orElseThrow().message()));
            }

            var id = r.result().orElseThrow();
            var func = overrides.get(id);
            if (func != null) {
                supplier = () -> ReflectiveValueConverter.convert(func.apply(LOCAL.get()));
                varCache.put(variable, supplier);
                return supplier.get();
            }

            var param = ExtractionTypes.getParameter(id);
            if (param == null)
                throw new CmdEvalException("%s is not a registered loot context parameter, variable or override!".formatted(id));
            supplier = () -> {
                var object = LOCAL.get().get(param);
                if (object == null) return null;
                return ReflectiveValueConverter.convert(object);
            };
            varCache.put(variable, supplier);
            return supplier.get();
        }

        @Override
        public void setData(String variable, EvaluationValue value) {
            parameters.put(variable, value);

            if (value == null) {
                varCache.remove(variable);
            } else {
                varCache.put(variable, () -> parameters.get(variable)); //We're already here, so might as well cache.
            }
        }
    }

    public static class SimpleFunctionDictionary implements FunctionDictionaryIfc {

        private final Map<String, FunctionIfc> functions = new Object2ReferenceOpenHashMap<>();

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
