package me.melontini.commander.impl.util.macro;

import com.mojang.serialization.DataResult;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.event.data.types.MacroTypes;
import me.melontini.commander.impl.util.ExpressionParser;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.melontini.commander.impl.util.ExpressionParser.*;

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
        List<MatchResult> matches = ExpressionParser.SELECTOR_PATTER.matcher(expression).results().toList();
        if (matches.size() == 1) {
            var r = evalSingular(matches.get(0));
            if (r != null) return r;
        }
        if (cast != null && !cast.equals("long")) return DataResult.error(() -> "Unknown cast type %s".formatted(cast));

        var result = ExpressionParser.evalExpression(matches, expression);
        if (cast != null) return result.map(function -> context -> String.valueOf((long) function.apply(context)));
        return result.map(function -> context -> String.valueOf(function.apply(context)));
    }

    public static DataResult<Function<LootContext, String>> evalSingular(MatchResult match) {
        String id = match.group(CONTEXT);
        String field = match.group(FIELD);
        String dynamic = match.group(DYNAMIC);

        DataResult<Identifier> idResult = Identifier.validate(id);
        if (idResult.error().isPresent()) return idResult.map(r -> null);
        Identifier identifier = idResult.result().orElseThrow();

        LootContextParameter<?> parameter = MacroTypes.knowParameter(identifier);
        if (parameter == null) return DataResult.error(() -> "Unknown loot context parameter %s".formatted(id));

        MacroContainer container = MacroTypes.getMacros(parameter);
        if (container.isArithmetic(field)) return null;
        if (!container.contains(field))
            return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));
        boolean isDynamic = container.isDynamic(field);
        if (isDynamic && dynamic == null)
            throw new IllegalStateException("Missing required dynamic for field %s".formatted(field));

        if (isDynamic) {
            var entry = container.ofDynamicString(field);
            Object value = entry.transformer().apply(dynamic);
            return DataResult.success(context -> entry.string().apply(value, context));
        } else {
            var extractor = container.ofString(field);
            return DataResult.success(extractor);
        }
    }
}
