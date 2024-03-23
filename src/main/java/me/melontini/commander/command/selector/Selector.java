package me.melontini.commander.command.selector;

import me.melontini.commander.event.EventContext;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public interface Selector {
    @Nullable ServerCommandSource select(EventContext context);
}
