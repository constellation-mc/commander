package me.melontini.commander.impl.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.api.util.functions.ToDoubleFunction;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.util.macro.ExtractionContainer;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
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

    public static final int CONTEXT = 1;
    public static final int FIELD = 2;
    public static final int DYNAMIC = 3;

    public static DataResult<Arithmetica> parseEither(Either<Double, String> either) {
        return either.map(d -> DataResult.success(Arithmetica.constant(d)), expression -> parseExpression(SELECTOR_PATTER.matcher(expression).results().toList(), expression));
    }

    public static DataResult<Arithmetica> parseExpression(List<MatchResult> matches, String expression) {
        Map<String, ToDoubleFunction<LootContext>> functions = new HashMap<>();
        Map<String, String> reps = new HashMap<>();
        for (MatchResult match : matches) {
            String id = match.group(CONTEXT);
            String field = match.group(FIELD);
            String dynamic = match.group(DYNAMIC);

            DataResult<Identifier> idResult = Identifier.validate(id);
            if (idResult.error().isPresent()) return idResult.map(r -> null);
            Identifier identifier = idResult.result().orElseThrow();

            LootContextParameter<?> parameter = ExtractionTypes.knownParameter(identifier);
            if (parameter == null) return DataResult.error(() -> "Unknown loot context parameter %s".formatted(id));

            ExtractionContainer container = ExtractionTypes.getMacros(parameter);
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
                functions.put(clear, context -> entry.arithmetic().apply(value, context));
            } else {
                var extractor = container.ofDouble(field);
                functions.put(clear, extractor);
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

            return DataResult.success(Arithmetica.of(context -> {
                synchronized (built) {
                    functions.forEach((string, function) -> built.setVariable(string, function.apply(context)));
                    return built.evaluate();
                }
            }, expression));
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
