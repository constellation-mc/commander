package me.melontini.commander.api.event;

import me.melontini.commander.impl.event.EventContextImpl;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface EventContext {

    static EventContext.Builder builder(EventType type) {
        return new EventContextImpl.Builder(type);
    }

    <T> @NotNull T getParameter(EventKey<T> key);
    @NotNull LootContext lootContext();

    void setReturnValue(Object value);
    <T> T getReturnValue(T def);

    EventContext with(Map<EventKey<?>, Object> parameters);

    interface Builder {
        <T> Builder addParameter(EventKey<T> key, T value);
        EventContext build();
    }
}
