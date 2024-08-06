package me.melontini.commander.api.event;

import java.util.IdentityHashMap;
import me.melontini.commander.impl.event.EventContextImpl;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface EventContext {

  @Contract("_ -> new")
  static EventContext.@NotNull Builder builder(EventType type) {
    return new EventContextImpl.Builder(type);
  }

  @NotNull EventType type();

  <T> @NotNull T getParameter(@NotNull EventKey<T> key);

  @NotNull LootContext lootContext();

  void setReturnValue(Object value);

  <T> T getReturnValue(T def);

  @Contract("_ -> new")
  @NotNull EventContext with(@NotNull IdentityHashMap<EventKey<?>, Object> parameters);

  interface Builder {
    @Contract("_, _ -> this")
    <T> Builder addParameter(EventKey<T> key, T value);

    EventContext build();
  }
}
