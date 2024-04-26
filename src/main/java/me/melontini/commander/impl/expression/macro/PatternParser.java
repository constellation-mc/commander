package me.melontini.commander.impl.expression.macro;

import com.ezylang.evalex.data.EvaluationValue;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.melontini.commander.impl.expression.EvalUtils.evaluate;

public class PatternParser {

    public static final Pattern PATTERN = Pattern.compile("\\$(?:\\(([a-z]+)\\))?\\{\\{([^{}]*)\\}\\}");
    public static final Map<String, Function<EvaluationValue, String>> CONVERTERS = ImmutableMap.of(
            "bool", v -> String.valueOf(v.getBooleanValue()),
            "long", v -> String.valueOf(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
            "int", v -> String.valueOf(v.getNumberValue().setScale(0, RoundingMode.DOWN)),
            "double", v -> v.getNumberValue().toString()
    );

    public static final int CAST = 1;
    public static final int EXPRESSION = 2;

    public static DataResult<BrigadierMacro> parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.results().findAny().isEmpty()) return DataResult.success(new ConstantMacro(input));
        matcher.reset();

        Function<LootContext, StringBuilder> start = context -> new StringBuilder();
        while (matcher.find()) {
            var result = parseExpression(matcher.group(EXPRESSION), matcher.group(CAST));
            if (result.error().isPresent()) return result.map(e -> null);

            var func = result.result().orElseThrow();
            var cmd = sb(b -> matcher.appendReplacement(b, ""));
            var fin = start;
            start = context -> fin.apply(context).append(cmd).append(func.apply(context));
        }

        var cmd = sb(matcher::appendTail);
        var fin = start;
        start = context -> fin.apply(context).append(cmd);

        return DataResult.success(new DynamicMacro(input, start));
    }

    private static String sb(Consumer<StringBuilder> consumer) {
        var b = new StringBuilder();
        consumer.accept(b);
        return b.toString();
    }

    public static DataResult<Function<LootContext, String>> parseExpression(String expression, @Nullable String cast) {
        var result = EvalUtils.parseExpression(expression);
        if (cast != null) {
            var c = CONVERTERS.get(cast);
            if (c == null) return DataResult.error(() -> "Unknown cast type %s".formatted(cast));
            return result.map(exp -> context -> c.apply(evaluate(context, exp)));
        }
        return result.map(exp -> context -> evaluate(context, exp).getStringValue());
    }
}
