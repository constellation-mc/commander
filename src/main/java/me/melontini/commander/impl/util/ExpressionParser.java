package me.melontini.commander.impl.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import me.melontini.commander.api.command.selector.Selector;
import me.melontini.commander.api.util.Arithmetica;
import me.melontini.commander.api.util.functions.ToDoubleFunction;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import me.melontini.commander.impl.util.macro.MacroContainer;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ExpressionParser {
    public static final Pattern SELECTOR_PATTER = Pattern.compile("([a-z0-9_\\-/.:]+)\\[([a-z0-9_\\-/.]+)(?:\\$([a-z0-9_\\-/.:]+))?\\]");

    public static DataResult<Arithmetica> parseArithmetica(Either<Double, String> either) {
        return either.map(d -> DataResult.success(Arithmetica.constant(d)), expression ->
                evalExpression(SELECTOR_PATTER.matcher(expression).results().toList(), expression)
                        .map(function -> Arithmetica.of(function, expression)));
    }

    public static DataResult<ToDoubleFunction<LootContext>> evalExpression(List<MatchResult> matches, String expression) {
        Map<String, ToDoubleFunction<LootContext>> functions = new HashMap<>();
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
            if (!container.contains(field))
                return DataResult.error(() -> "Unknown field type %s for selector %s".formatted(field, id));
            if (!container.isArithmetic(field))
                return DataResult.error(() -> "String fields are not supported in arithmetic expressions: %s".formatted(field));
            boolean isDynamic = container.isDynamic(field);
            if (isDynamic && dynamic == null)
                throw new IllegalStateException("Missing required dynamic for field %s".formatted(field));

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

        try {
            for (Map.Entry<String, String> e : reps.entrySet()) {
                expression = expression.replace(e.getKey(), e.getValue());
            }
            ExpressionBuilder builder = new ExpressionBuilder(expression);
            StdFunctions.FUNCTIONS.forEach(builder::function);
            functions.keySet().forEach(builder::variable);
            Expression built = builder.build();

            return DataResult.success(context -> {
                synchronized (built) {
                    functions.forEach((string, function) -> built.setVariable(string, function.apply(context)));
                    return built.evaluate();
                }
            });
        } catch (Throwable throwable) {
            return DataResult.error(throwable::getLocalizedMessage);
        }
    }

    private static String sanitize(String s) {
        return s.replace(":", "_cl_")
                .replace("[", "_rb_")
                .replace("]", "_lb_")
                .replace("/", "_lsl_")
                .replace("$", "_dl_");
    }
}
