package me.melontini.commander.impl.util;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.FunctionDictionaryIfc;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.functions.datetime.*;
import com.ezylang.evalex.functions.string.*;
import com.ezylang.evalex.functions.trigonometric.*;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.melontini.commander.impl.util.functions.ClampFunction;
import me.melontini.commander.impl.util.functions.LerpFunction;
import me.melontini.commander.impl.util.functions.RangedRandomFunction;

import java.math.BigDecimal;
import java.util.Map;

public class EvalUtils {

    public static final ExpressionConfiguration CONFIGURATION = ExpressionConfiguration.builder()
            .dataAccessorSupplier(MapBasedDataAccessor::new)
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

    public static class MapBasedDataAccessor implements DataAccessorIfc {

        private final Map<String, EvaluationValue> variables = new Object2ObjectOpenHashMap<>();

        @Override
        public EvaluationValue getData(String variable) {
            return variables.get(variable);
        }

        @Override
        public void setData(String variable, EvaluationValue value) {
            variables.put(variable, value);
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
        public FunctionIfc getFunction(String functionName) {
            return functions.get(functionName);
        }

        @Override
        public void addFunction(String functionName, FunctionIfc function) {
            functions.put(functionName, function);
        }
    }

    public static void init() {}
}
