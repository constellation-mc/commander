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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser {

    public static final Pattern PATTERN = Pattern.compile("\\$\\{\\{([^{}]*)\\}\\}");
    public static final Pattern SELECTOR_PATTER = Pattern.compile("([a-z0-9_\\-/.:]+)\\[([a-z0-9_\\-/.]+)(?:\\$([a-z0-9_\\-/.:]+))?\\]");

    public static DataResult<BrigadierMacro> parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.results().findAny().isEmpty()) return DataResult.success(new ConstantMacro(input));
        matcher.reset();

        Function<EventContext, StringBuilder> start = context -> new StringBuilder();
        while (matcher.find()) {
            var result = parseExpression(matcher.group(1));
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

    public static DataResult<Function<EventContext, String>> parseExpression(String expression) {
        List<MatchResult> matches = SELECTOR_PATTER.matcher(expression).results().toList();
        if (matches.isEmpty()) return DataResult.error(() -> "Invalid expression %s".formatted(expression));

        if (matches.size() == 1) {
            var r = evalSingular(matches.get(0));
            if (r != null) return r;
        }
        return evalExpression(matches, expression);
    }

    public static DataResult<Function<EventContext, String>> evalExpression(List<MatchResult> matches, String expression) {
        Map<String, ToDoubleFunction<EventContext>> functions = new HashMap<>();
        Map<String, String> reps = new HashMap<>();
        for (MatchResult match : matches) {
            String id = match.group(1);
            String field = match.group(2);
            String dynamic = match.group(3);

            DataResult<Identifier> idResult = Identifier.validate(id);
            if (idResult.error().isPresent()) return idResult.map(r -> null);
            Identifier identifier = idResult.result().orElseThrow();

            Selector selector = SelectorTypes.getSelector(identifier);
            if (selector == null) return DataResult.error(() -> "Unknown selector type %s!".formatted(id));

            MacroContainer container = SelectorTypes.getMacros(identifier);
            if (!container.contains(field)) return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));
            if (!container.isArithmetic(field)) return DataResult.error(() -> "String fields are not supported in arithmetic expressions: %s".formatted(field));
            boolean isDynamic = container.isDynamic(field);
            if (isDynamic && dynamic == null) throw new IllegalStateException("Missing required dynamic for field %s".formatted(field));

            String part = expression.substring(match.start(), match.end());
            var clear = sanitize(part);
            reps.put(part, clear);

            if (isDynamic) {
                var entry = container.ofDynamicDouble(field);
                Object value = entry.transformer().apply(dynamic);
                functions.put(clear, context -> entry.arithmetic().apply(value, selector.select(context)));
            } else {
                var extractor = container.ofDouble(field);
                functions.put(clear, context -> extractor.apply(selector.select(context)));
            }
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
                .replace("[", "_rb_")
                .replace("]", "_lb_")
                .replace("/", "_lsl_")
                .replace("$", "_dl_");
    }

    public static DataResult<Function<EventContext, String>> evalSingular(MatchResult match) {
        String id = match.group(1);
        String field = match.group(2);
        String dynamic = match.group(3);

        DataResult<Identifier> idResult = Identifier.validate(id);
        if (idResult.error().isPresent()) return idResult.map(r -> null);
        Identifier identifier = idResult.result().orElseThrow();

        MacroContainer container = SelectorTypes.getMacros(identifier);
        if (container.isArithmetic(field)) return null;
        if (!container.contains(field)) return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));
        boolean isDynamic = container.isDynamic(field);
        if (isDynamic && dynamic == null) throw new IllegalStateException("Missing required dynamic for field %s".formatted(field));

        Selector selector = SelectorTypes.getSelector(identifier);
        if (selector == null) return DataResult.error(() -> "Unknown selector type %s!".formatted(id));

        if (isDynamic) {
            var entry = container.ofDynamicString(field);
            Object value = entry.transformer().apply(dynamic);
            return DataResult.success(context -> entry.string().apply(value, selector.select(context)));
        } else {
            var extractor = container.ofString(field);
            return DataResult.success(context -> extractor.apply(selector.select(context)));
        }
    }
}
