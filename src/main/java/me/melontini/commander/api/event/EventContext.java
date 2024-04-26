package me.melontini.commander.api.event;

import me.melontini.commander.impl.event.EventContextImpl;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

public interface EventContext {

    @Contract("_ -> new")
    static EventContext.@NotNull Builder builder(EventType type) {
        return new EventContextImpl.Builder(type);
    }

    EventType type();

    <T> @NotNull T getParameter(EventKey<T> key);
    @NotNull LootContext lootContext();

    void setReturnValue(Object value);
    <T> T getReturnValue(T def);

    EventContext with(IdentityHashMap<EventKey<?>, Object> parameters);

    interface Builder {
        <T> Builder addParameter(EventKey<T> key, T value);
        EventContext build();
    }
}
