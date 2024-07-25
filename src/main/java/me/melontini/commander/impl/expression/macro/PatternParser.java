package me.melontini.commander.impl.expression.macro;

import static me.melontini.commander.impl.expression.EvalUtils.evaluate;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.BooleanValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

public class PatternParser {

  public static final Pattern PATTERN =
      Pattern.compile("\\$(?:\\(([a-z]+)\\))?\\{\\{([^{}]*)\\}\\}");
  public static final Map<String, Function<EvaluationValue, EvaluationValue>> CONVERTERS =
      ImmutableMap.of(
          "bool", v -> BooleanValue.of(v.getBooleanValue()),
          "long", v -> NumberValue.of(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
          "int", v -> NumberValue.of(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
          "double", v -> NumberValue.of(v.getNumberValue()));

  public static final int CAST = 1;
  public static final int EXPRESSION = 2;

  public static DataResult<BrigadierMacro> parse(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.results().findAny().isEmpty()) return DataResult.success(new ConstantMacro(input));
    matcher.reset();

    BiFunction<LootContext, Map<String, Object>, StringBuilder> start =
        (context, params) -> new StringBuilder();
    while (matcher.find()) {
      var result = parseExpression(matcher.group(EXPRESSION), matcher.group(CAST));
      if (result.error().isPresent()) return result.map(e -> null);

      var func = result.result().orElseThrow();
      var cmd = sb(b -> matcher.appendReplacement(b, ""));
      var fin = start;
      start = (context, params) -> fin.apply(context, params)
          .append(cmd)
          .append(EvalUtils.toMacroString(func.apply(context, params)));
    }

    var cmd = sb(matcher::appendTail);
    var fin = start;
    start = (context, params) -> fin.apply(context, params).append(cmd);

    return DataResult.success(new DynamicMacro(input, start));
  }

  private static String sb(Consumer<StringBuilder> consumer) {
    var b = new StringBuilder();
    consumer.accept(b);
    return b.toString();
  }

  public static DataResult<BiFunction<LootContext, Map<String, ?>, EvaluationValue>>
      parseExpression(String expression, @Nullable String cast) {
    if (cast == null)
      return EvalUtils.parseExpression(expression)
          .map(exp -> (context, params) -> evaluate(context, exp, params));

    var c = CONVERTERS.get(cast);
    if (c == null) return DataResult.error(() -> "Unknown cast type %s".formatted(cast));
    return EvalUtils.parseExpression(expression).map(exp -> (context, params) -> {
      var evalResult = evaluate(context, exp, params);
      return evalResult.getValue() == null ? null : c.apply(evalResult);
    });
  }
}
