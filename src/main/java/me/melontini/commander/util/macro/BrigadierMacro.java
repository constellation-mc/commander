package me.melontini.commander.util.macro;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.command.selector.Extractor;
import me.melontini.commander.command.selector.Selector;
import me.melontini.commander.data.types.SelectorTypes;
import me.melontini.commander.event.EventContext;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface BrigadierMacro {

    Pattern PATTERN = Pattern.compile("\\$\\{\\{([^}]*)\\(([^)]*)\\)\\}\\}");
    Codec<BrigadierMacro> CODEC = Codec.STRING.comapFlatMap(BrigadierMacro::buildCommand, BrigadierMacro::original);

    static DataResult<BrigadierMacro> buildCommand(String input) {
        Matcher matcher = PATTERN.matcher(input);

        int hits = 0;
        Function<EventContext, String> start = context -> "";
        while (matcher.find()) {
            hits++;
            DataResult<Identifier> identifier = Identifier.validate(matcher.group(1));
            if (identifier.error().isPresent()) return identifier.map(i -> null);
            Selector selector = SelectorTypes.getSelector(identifier.result().orElseThrow());

            String field = matcher.group(2);
            Extractor extractor = SelectorTypes.getExtractor(identifier.result().orElseThrow(), field);
            if (extractor == null) return DataResult.error(() -> "Selector %s does not support extractor %s".formatted(identifier, field));

            var builder = new StringBuilder();
            matcher.appendReplacement(builder,"");
            String cmd = builder.toString();
            Function<EventContext, String> finalStart = start;
            start = context -> finalStart.apply(context) + cmd + extractor.apply(Objects.requireNonNull(selector.select(context)));
        }
        if (hits == 0)  return DataResult.success(new ConstantMacro(input));
        var builder = new StringBuilder();
        matcher.appendTail(builder);
        String cmd = builder.toString();
        Function<EventContext, String> finalStart = start;
        start = context -> finalStart.apply(context) + cmd;
        return DataResult.success(new DynamicMacro(input, start));
    }

    String build(EventContext context);
    String original();
}
