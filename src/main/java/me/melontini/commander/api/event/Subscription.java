package me.melontini.commander.api.event;

import me.melontini.commander.api.command.Command;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface Subscription<E> {

    static <T> T getData(MinecraftServer server, EventType type) {
        return DynamicEventManager.getData(server, type);
    }

    EventType type();
    E parameters();
    List<Command.Conditioned> list();
}
