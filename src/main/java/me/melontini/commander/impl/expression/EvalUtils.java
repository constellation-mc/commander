package me.melontini.commander.impl.expression;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.FunctionDictionaryIfc;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.functions.datetime.*;
import com.ezylang.evalex.functions.string.*;
import com.ezylang.evalex.functions.trigonometric.*;
import com.ezylang.evalex.parser.ParseException;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.util.functions.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class EvalUtils {

    public static final ExpressionConfiguration CONFIGURATION = ExpressionConfiguration.builder()
            .dataAccessorSupplier(MapBasedDataAccessor::new)
            .evaluationValueConverter(new ReflectiveValueConverter())
            .defaultConstants(ImmutableMap.of(
                    "true", EvaluationValue.booleanValue(true),
                    "false", EvaluationValue.booleanValue(false),
                    "PI", EvaluationValue.numberValue(new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")),
                    "E", EvaluationValue.numberValue(new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663")),
                    "null", EvaluationValue.nullValue(),
                    "DT_FORMAT_ISO_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]['['VV']']"),
                    "DT_FORMAT_LOCAL_DATE_TIME", EvaluationValue.stringValue("yyyy-MM-dd'T'HH:mm:ss[.SSS]"),
                    "DT_FORMAT_LOCAL_DATE", EvaluationValue.stringValue("yyyy-MM-dd")
            ))
            .functionDictionary(MapBasedFunctionDictionary.ofFunctions(
                    ImmutableMap.<String, FunctionIfc>builder()
                            // basic functions
                            .put("abs", new AbsFunction())
                            .put("ceil", new CeilingFunction())
                            .put("coalesce", new CoalesceFunction())
                            .put("fact", new FactFunction())
                            .put("floor", new FloorFunction())
                            .put("if", new IfFunction())
                            .put("log", new LogFunction())
                            .put("log10", new Log10Function())
                            .put("max", new MaxFunction())
                            .put("min", new MinFunction())
                            .put("not", new NotFunction())
                            .put("round", new RoundFunction())
                            .put("sum", new SumFunction())
                            .put("sqrt", new SqrtFunction())
                            .put("random", new RangedRandomFunction())
                            .put("lerp", new LerpFunction())
                            .put("clamp", new ClampFunction())
                            //maps
                            .put("structContainsKey", new StructContainsKeyFunction())
                            //meta
                            .put("hasContext", new HasContextFunction())
                            // trigonometric
                            .put("acos", new AcosFunction())
                            .put("acosh", new AcosHFunction())
                            .put("acosr", new AcosRFunction())
                            .put("acot", new AcotFunction())
                            .put("acoth", new AcotHFunction())
                            .put("acotr", new AcotRFunction())
                            .put("asin", new AsinFunction())
                            .put("asinh", new AsinHFunction())
                            .put("asinr", new AsinRFunction())
                            .put("atan", new AtanFunction())
                            .put("atan2", new Atan2Function())
                            .put("atan2r", new Atan2RFunction())
                            .put("atanh", new AtanHFunction())
                            .put("atanr", new AtanRFunction())
                            .put("cos", new CosFunction())
                            .put("cosh", new CosHFunction())
                            .put("cosr", new CosRFunction())
                            .put("cot", new CotFunction())
                            .put("coth", new CotHFunction())
                            .put("cotr", new CotRFunction())
                            .put("csc", new CscFunction())
                            .put("csch", new CscHFunction())
                            .put("cscr", new CscRFunction())
                            .put("deg", new DegFunction())
                            .put("rad", new RadFunction())
                            .put("sin", new SinFunction())
                            .put("sinh", new SinHFunction())
                            .put("sinr", new SinRFunction())
                            .put("sec", new SecFunction())
                            .put("sech", new SecHFunction())
                            .put("secr", new SecRFunction())
                            .put("tan", new TanFunction())
                            .put("tanh", new TanHFunction())
                            .put("tanr", new TanRFunction())
                            // string functions
                            .put("strContains", new StringContains())
                            .put("strEndsWith", new StringEndsWithFunction())
                            .put("strLower", new StringLowerFunction())
                            .put("strStartsWith", new StringStartsWithFunction())
                            .put("strUpper", new StringUpperFunction())
                            // date time functions
                            .put("dtDateNew", new DateTimeNewFunction())
                            .put("dtDateParse", new DateTimeParseFunction())
                            .put("dtDateFormat", new DateTimeFormatFunction())
                            .put("dtDateToEpoch", new DateTimeToEpochFunction())
                            .put("dtDurationNew", new DurationNewFunction())
                            .put("dtDurationFromMillis", new DurationFromMillisFunction())
                            .put("dtDurationToMillis", new DurationToMillisFunction())
                            .put("dtDurationParse", new DurationParseFunction())
                            .put("dtNow", new DateTimeNowFunction())
                            .put("dtToday", new DateTimeTodayFunction())
                            .build()))
            .build();

    public static EvaluationValue evaluate(LootContext context, Expression exp) {
        try {
            MapBasedDataAccessor.LOCAL.set(context);
            return exp.evaluate();
        } catch (EvaluationException | ParseException e) {
            throw new CmdEvalException(Objects.requireNonNullElseGet(e.getMessage(), () -> "Failed to evaluate expression %s".formatted(exp.getExpressionString().replace("__idcl__", ":"))), e);
        } finally {
            MapBasedDataAccessor.LOCAL.remove();
        }
    }

    public static DataResult<Arithmetica> parseEither(Either<Double, String> either) {
        return either.map(d -> DataResult.success(Arithmetica.constant(d)), string -> parseExpression(string).map(exp -> Arithmetica.of(context -> evaluate(context, exp).getNumberValue().doubleValue(), string)));
    }

    public static DataResult<Expression> parseExpression(String expression) {
        try {
            Expression exp = new Expression(expression.replace(":", "__idcl__"), CONFIGURATION);
            exp.validate();
            return DataResult.success(exp);
        } catch (Throwable throwable) {
            return DataResult.error(throwable::getLocalizedMessage);
        }
    }

    public static class MapBasedDataAccessor implements DataAccessorIfc {

        private static final Map<Identifier, Function<LootContext, Object>> overrides = ImmutableMap.of(
                new Identifier("level"), LootContext::getWorld,
                new Identifier("luck"), LootContext::getLuck
        );
        public static final ThreadLocal<LootContext> LOCAL = new ThreadLocal<>();

        @Override
        public @Nullable EvaluationValue getData(String variable) {
            var r = Identifier.validate(variable.replace("__idcl__", ":"));
            if (r.error().isPresent()) {
                throw new CmdEvalException("%s - %s".formatted(variable.replace("__idcl__", ":"), r.error().orElseThrow().message()));
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
            throw new IllegalStateException(variable);
        }
    }

    public static class MapBasedFunctionDictionary implements FunctionDictionaryIfc {

        private final Map<String, FunctionIfc> functions = new Object2ObjectOpenHashMap<>();

        public static FunctionDictionaryIfc ofFunctions(Map<String, FunctionIfc> functions) {
            FunctionDictionaryIfc dictionary = new MapBasedFunctionDictionary();
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

    public static void init() {}
}
