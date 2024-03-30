package me.melontini.commander.command.selector;

import me.melontini.commander.data.types.SelectorTypes;
import me.melontini.commander.event.EventContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public interface Selector {

    static Selector register(Identifier identifier, Selector selector) {
        return SelectorTypes.register(identifier, selector, null);
    }

    static Selector register(Identifier identifier, Selector selector, Consumer<Map<String, Extractor>> extractors) {
        return SelectorTypes.register(identifier, selector, extractors);
    }

    @Nullable ServerCommandSource select(EventContext context);
}
