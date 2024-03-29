package me.melontini.commander.command.selector;

import me.melontini.commander.data.types.SelectorTypes;
import me.melontini.commander.event.EventContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface Selector {

    static Selector register(Identifier identifier, Selector selector) {
        return SelectorTypes.register(identifier, selector);
    }

    @Nullable ServerCommandSource select(EventContext context);
}
