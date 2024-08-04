package me.melontini.commander.api.event;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Subscription<E> {

  /**
   * Returns all data for the event from the current server.
   * @param server The current server instance.
   * @param type The event type.
   * @return Data associated with the event or null if the {@link EventType.Builder#extension(Codec, Function)} returns null.
   */
  static <T> @Nullable T getData(MinecraftServer server, EventType type) {
    return DynamicEventManager.getData(server, type);
  }

  /**
   * @return Subscribed event type.
   */
  EventType type();

  /**
   * @return Event parameters or null if {@link EventType.Builder#extension(Codec, Function)} was not specified.
   */
  @Nullable E parameters();

  /**
   * @return {@link Command.Conditioned} parsed from the subscription file.
   */
  List<Command.Conditioned> list();
}
