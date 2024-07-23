package me.melontini.commander.api.event;

import java.util.List;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public interface Subscription<E> {

  static <T> @Nullable T getData(MinecraftServer server, EventType type) {
    return DynamicEventManager.getData(server, type);
  }

  EventType type();

  E parameters();

  List<Command.Conditioned> list();
}
