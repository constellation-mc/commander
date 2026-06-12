package dev.zenfyr.commander.impl.expression;

import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.functions.datetime.*;
import com.ezylang.evalex.functions.string.*;
import com.ezylang.evalex.functions.trigonometric.*;
import dev.zenfyr.commander.impl.expression.function.math.ClampFunction;
import dev.zenfyr.commander.impl.expression.function.math.LerpFunction;
import dev.zenfyr.commander.impl.expression.function.math.RangedRandomFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;

public class FunctionContainers {

  public static Map<String, FunctionIfc> initMath() {
    Map<String, FunctionIfc> math = new Object2ReferenceOpenHashMap<>();

    // built-in math
    math.put("abs", new AbsFunction());
    math.put("average", new AverageFunction());
    math.put("ceiling", new CeilingFunction());
    math.put("coalesce", new CoalesceFunction());
    math.put("fact", new FactFunction());
    math.put("floor", new FloorFunction());
    math.put("log", new LogFunction());
    math.put("log10", new Log10Function());
    math.put("max", new MaxFunction());
    math.put("min", new MinFunction());
    math.put("round", new RoundFunction());
    math.put("sqrt", new SqrtFunction());
    math.put("sum", new SumFunction());

    // built-in trigonometry
    math.put("acos", new AcosFunction());
    math.put("acosh", new AcosHFunction());
    math.put("acosr", new AcosRFunction());
    math.put("acot", new AcotFunction());
    math.put("acoth", new AcotHFunction());
    math.put("acotr", new AcotRFunction());
    math.put("asin", new AsinFunction());
    math.put("asinh", new AsinHFunction());
    math.put("asinr", new AsinRFunction());
    math.put("atan", new AtanFunction());
    math.put("atan2", new Atan2Function());
    math.put("atan2r", new Atan2RFunction());
    math.put("atanh", new AtanHFunction());
    math.put("atanr", new AtanRFunction());
    math.put("cos", new CosFunction());
    math.put("cosh", new CosHFunction());
    math.put("coer", new CosRFunction());
    math.put("cot", new CotFunction());
    math.put("coth", new CotHFunction());
    math.put("cotr", new CotRFunction());
    math.put("csc", new CscFunction());
    math.put("csch", new CscHFunction());
    math.put("cscr", new CscRFunction());
    math.put("deg", new DegFunction());
    math.put("rad", new RadFunction());
    math.put("sin", new SinFunction());
    math.put("sinh", new SinHFunction());
    math.put("sinr", new SinRFunction());
    math.put("sec", new SecFunction());
    math.put("sech", new SecHFunction());
    math.put("secr", new SecRFunction());
    math.put("tan", new TanFunction());
    math.put("tanh", new TanHFunction());
    math.put("tanr", new TanRFunction());

    // our custom functions

    math.put("random", new RangedRandomFunction());
    math.put("lerp", new LerpFunction());
    math.put("clamp", new ClampFunction());

    return math;
  }

  public static Map<String, FunctionIfc> initDateTime() {
    Map<String, FunctionIfc> dateTime = new Object2ReferenceOpenHashMap<>();

    dateTime.put("new", new DateTimeNewFunction());
    dateTime.put("parse", new DateTimeParseFunction());
    dateTime.put("format", new DateTimeFormatFunction());
    dateTime.put("toEpoch", new DateTimeToEpochFunction());
    dateTime.put("now", new DateTimeNowFunction());
    dateTime.put("today", new DateTimeTodayFunction());

    return dateTime;
  }

  public static Map<String, FunctionIfc> initDurations() {
    Map<String, FunctionIfc> durations = new Object2ReferenceOpenHashMap<>();

    durations.put("new", new DurationNewFunction());
    durations.put("fromMillis", new DurationFromMillisFunction());
    durations.put("toMillis", new DurationToMillisFunction());
    durations.put("parse", new DurationParseFunction());

    return durations;
  }
}
