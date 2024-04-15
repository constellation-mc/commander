package me.melontini.commander.impl.util.macro;

import com.mojang.serialization.DataResult;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.util.eval.EvalUtils;
import net.minecraft.loot.context.LootContext;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser {

    public static final Pattern PATTERN = Pattern.compile("\\$(?:\\(([a-z]+)\\))?\\{\\{([^{}]*)\\}\\}");

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

    public static DataResult<Function<LootContext, String>> parseExpression(String expression, String cast) {
        var result = EvalUtils.parseExpression(expression);
        if (cast != null) {
            return switch (cast) {
                case "long" -> result.map(function -> context -> String.valueOf(function.apply(context).getNumberValue().longValue()));
                case "int" -> result.map(function -> context -> String.valueOf(function.apply(context).getNumberValue().intValue()));
                case "bool" -> result.map(function -> context -> String.valueOf(function.apply(context).getBooleanValue()));
                default -> DataResult.error(() -> "Unknown cast type %s".formatted(cast));
            };
        }
        return result.map(function -> context -> function.apply(context).getStringValue());
    }
}
