package me.melontini.commander.util.macro;

import com.mojang.serialization.DataResult;
import me.melontini.commander.command.selector.Selector;
import me.melontini.commander.data.types.SelectorTypes;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.util.functions.ToDoubleFunction;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser {

    public static final Pattern PATTERN = Pattern.compile("\\$\\{\\{([^{}]*)\\}\\}");
    public static final Pattern SELECTOR_PATTER = Pattern.compile("([a-z0-9_\\-/.:]*)\\(([a-z0-9_\\-/.]*)\\)");

    public static DataResult<BrigadierMacro> parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.results().findAny().isEmpty()) return DataResult.success(new ConstantMacro(input));
        matcher.reset();

        Function<EventContext, String> start = context -> "";
        while (matcher.find()) {
            var result = parseExpression(matcher.group(1));
            if (result.error().isPresent()) return result.map(e -> null);

            var func = result.result().orElseThrow();
            var cmd = sb(b -> matcher.appendReplacement(b, ""));
            var fin = start;
            start = context -> fin.apply(context) + cmd + func.apply(context);
        }

        var cmd = sb(matcher::appendTail);
        var fin = start;
        start = context -> fin.apply(context) + cmd;

        return DataResult.success(new DynamicMacro(input, start));
    }

    private static String sb(Consumer<StringBuilder> consumer) {
        var b = new StringBuilder();
        consumer.accept(b);
        return b.toString();
    }

    public static DataResult<Function<EventContext, String>> parseExpression(String expression) {
        Matcher matcher = SELECTOR_PATTER.matcher(expression);
        if (matcher.results().findAny().isEmpty()) return DataResult.error(() -> "Invalid expression %s".formatted(expression));
        matcher.reset();

        long matches = matcher.results().count();
        matcher.reset();
        if (matches == 1) return evalSingular(matcher);

        return evalExpression(matcher, expression);
    }

    public static DataResult<Function<EventContext, String>> evalExpression(Matcher matcher, String expression) {
        Map<String, ToDoubleFunction<EventContext>> functions = new HashMap<>();
        Map<String, String> reps = new HashMap<>();
        while (matcher.find()) {
            String id = matcher.group(1);
            String field = matcher.group(2);

            DataResult<Identifier> idResult = Identifier.validate(id);
            if (idResult.error().isPresent()) return idResult.map(r -> null);
            Identifier identifier = idResult.result().orElseThrow();

            Selector selector = SelectorTypes.getSelector(identifier);
            if (selector == null) return DataResult.error(() -> "Unknown selector type %s!".formatted(id));

            MacroContainer container = SelectorTypes.getMacros(identifier);
            if (!container.contains(field)) return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));
            if (!container.isArithmetic(field)) return DataResult.error(() -> "String fields are not supported in arithmetic expressions: %s".formatted(field));

            var extractor = container.ofDouble(field);
            var clear = sanitize(id + "(" + field + ")");
            functions.put(clear, input -> extractor.apply(selector.select(input)));
            reps.put(id + "(" + field + ")", clear);
        }

        for (Map.Entry<String, String> e : reps.entrySet()) {
            expression = expression.replace(e.getKey(), e.getValue());
        }
        ExpressionBuilder builder = new ExpressionBuilder(expression);
        functions.keySet().forEach(builder::variable);
        Expression built = builder.build();

        return DataResult.success(context -> {
            functions.forEach((string, function) -> built.setVariable(string, function.apply(context)));
            return String.valueOf(built.evaluate());
        });
    }

    private static String sanitize(String s) {
        return s.replace(":", "_cl_")
                .replace("(", "_rp_")
                .replace(")", "_lp_")
                .replace("/", "_lsl_");
    }

    public static DataResult<Function<EventContext, String>> evalSingular(Matcher matcher) {
        if (!matcher.find()) return DataResult.error(() -> "Illegal eval state");
        String id = matcher.group(1);
        String field = matcher.group(2);

        DataResult<Identifier> idResult = Identifier.validate(id);
        if (idResult.error().isPresent()) return idResult.map(r -> null);
        Identifier identifier = idResult.result().orElseThrow();

        Selector selector = SelectorTypes.getSelector(identifier);
        if (selector == null) return DataResult.error(() -> "Unknown selector type %s!".formatted(id));

        MacroContainer container = SelectorTypes.getMacros(identifier);
        if (!container.contains(field)) return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));

        if (container.isArithmetic(field)) {
            var extractor = container.ofDouble(field);
            return DataResult.success(context -> String.valueOf(extractor.apply(selector.select(context))));
        }
        var extractor = container.ofString(field);
        return DataResult.success(context -> extractor.apply(selector.select(context)));
    }
}
