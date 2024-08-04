package me.melontini.commander.impl.expression.macro;

import static me.melontini.commander.impl.expression.EvalUtils.evaluate;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.BooleanValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.dark_matter.api.base.util.Result;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

public class PatternParser {

  public static final Map<String, Function<EvaluationValue, EvaluationValue>> CONVERTERS =
      ImmutableMap.of(
          "bool", v -> BooleanValue.of(v.getBooleanValue()),
          "long", v -> NumberValue.of(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
          "int", v -> NumberValue.of(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
          "double", v -> NumberValue.of(v.getNumberValue()));

  // Parses brigadier macros from strings
  public static DataResult<BrigadierMacro> parse(String input) {
    StringReader reader = new StringReader(input);

    List<Appender> list = new ArrayList<>();
    StringBuilder start = new StringBuilder();
    while (reader.hasNext()) {
      char c = reader.read();
      if (c == '$') { // Possible macro start
        var result = processMacroStart(reader);
        if (result.error().isPresent())
          return DataResult.error(() -> result.error().orElseThrow());
        if (result.value().isPresent()) {
          var expression = result.value().orElseThrow();
          var fin = start.toString();
          list.add((context, params, builder) -> builder
              .append(fin)
              .append(EvalUtils.toMacroString(expression.apply(context, params))));
          start = new StringBuilder();
          continue;
        }
      }
      start.append(c);
    }
    if (list.isEmpty()) { // Empty == no macros.
      return DataResult.success(new ConstantMacro(input));
    }

    var fin = start.toString();
    if (list.size() == 1) {
      var appender = list.get(0);
      return DataResult.success(new DynamicMacro(
          input,
          (context, params, builder) -> appender.build(context, params, builder).append(fin)));
    }
    list.add((context, params, builder) -> builder.append(fin));
    return DataResult.success(new DynamicMacro(
        input,
        list.stream()
            .reduce((a1, a2) -> (context, params, builder) ->
                a2.build(context, params, a1.build(context, params, builder)))
            .orElseThrow()));
  }

  public interface Appender {
    StringBuilder build(LootContext context, Map<String, ?> params, StringBuilder builder);
  }

  public static Result<BiFunction<LootContext, Map<String, ?>, EvaluationValue>, String>
      processMacroStart(StringReader reader) {
    int start = reader.pointer();

    // This might not be a macro start, but we have to pre-read
    // the cast in case of dangling `(`.
    StringBuilder cast = null;
    if (reader.peek() == '(')
      cast:
      {
        reader.skip();
        cast = new StringBuilder();
        while (reader.hasNext()) {
          char c = reader.read();
          if (c == ')') break cast;
          cast.append(c);
        }
        return Result.error("Dangling parentheses '(' at index %s".formatted(start));
      }

    StringBuilder expression = null;
    // Check if it *is* a macro start.
    if (reader.peek() == '{' && reader.peek(1) == '{')
      expression:
      {
        reader.skip(2);
        expression = new StringBuilder();
        while (reader.hasNext()) {
          char c = reader.read();
          if (c == '}' && reader.canRead(1) && reader.peek() == '}') {
            reader.skip();
            break expression;
          }
          expression.append(c);
        }
        return Result.error("Dangling braces '{{' at index %s".formatted(start));
      }

    // Nope, not a macro.
    if (expression == null) return Result.empty();
    if (cast != null) { // Now we can verify the cast.
      String s = cast.toString();
      if (s.isBlank()) return Result.error("Illegal empty cast at index %s".formatted(start));
      for (char c : s.toCharArray()) {
        if (!Character.isLetter(c)) // Only letters are allowed.
        return Result.error(
              "Illegal cast (%s). Must only contain letters and no whitespace!".formatted(s));
      }
      if (!CONVERTERS.containsKey(s)) return Result.error("No such cast (%s)".formatted(s));
    }

    var result = parseExpression(expression.toString(), cast == null ? null : cast.toString());
    if (result.error().isPresent()) return Result.error(result.error().get().message());
    return Result.ok(result.result().orElseThrow());
  }

  public static DataResult<BiFunction<LootContext, Map<String, ?>, EvaluationValue>>
      parseExpression(String expression, @Nullable String cast) {
    if (cast == null)
      return EvalUtils.parseExpression(expression)
          .map(exp -> (context, params) -> evaluate(context, exp, params));

    var c = CONVERTERS.get(cast);
    return EvalUtils.parseExpression(expression).map(exp -> (context, params) -> {
      var evalResult = evaluate(context, exp, params);
      return evalResult.getValue() == null ? null : c.apply(evalResult);
    });
  }

  public static class StringReader {
    private final String input;
    private int pointer = 0;

    public StringReader(String input) {
      this.input = input;
    }

    public char peek() {
      return this.peek(0);
    }

    public char peek(int offset) {
      return this.input.charAt(pointer + offset);
    }

    public char read() {
      return this.input.charAt(pointer++);
    }

    public void skip(int offset) {
      pointer += offset;
    }

    public void skip() {
      skip(1);
    }

    public boolean canRead(int length) {
      return pointer + length <= this.input.length();
    }

    public boolean hasNext() {
      return canRead(1);
    }

    public int pointer() {
      return pointer;
    }

    public void pointer(int pointer) {
      this.pointer = pointer;
    }
  }
}
