package me.melontini.commander.impl.event;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventKey;
import me.melontini.commander.api.event.EventType;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Accessors(fluent = true)
public final class EventContextImpl implements EventContext {

    @Getter
    private final EventType type;
    private final IdentityHashMap<EventKey<?>, Object> parameters;

    private EventContextImpl(EventType type, IdentityHashMap<EventKey<?>, Object> parameters) {
        this.parameters = parameters;
        this.type = type;
    }

    @Override
    public EventContextImpl with(IdentityHashMap<EventKey<?>, Object> parameters) {
        IdentityHashMap<EventKey<?>, Object> map = new IdentityHashMap<>(this.parameters);
        map.putAll(parameters);
        return new EventContextImpl(type, map);
    }

    @Override
    public <T> @NotNull T getParameter(EventKey<T> key) {
        var r = parameters.get(key);
        if (r == null) throw new IllegalStateException("Missing required parameter key %s".formatted(key.id()));
        return (T) r;
    }

    @Override
    public @NotNull LootContext lootContext() {
        return getParameter(EventKey.LOOT_CONTEXT);
    }

    @Override
    public void setReturnValue(Object value) {
        getParameter(EventKey.RETURN_VALUE).set(value);
    }

    @Override
    public <T> T getReturnValue(T def) {
        var r = getParameter(EventKey.RETURN_VALUE).get();
        if (r == null) return def;
        return (T) r;
    }

    public static Builder builder(EventTypeImpl type) {
        return new Builder(type);
    }

    public static final class Builder implements EventContext.Builder {
        private final IdentityHashMap<EventKey<?>, Object> map = new IdentityHashMap<>();
        private final EventType type;

        public Builder(EventType type) {
            this.type = type;

            if (this.type.get(EventType.CANCEL_TERM).isPresent()) {
                map.put(EventKey.RETURN_VALUE, new AtomicReference<>());
            }
        }

        @Override
        public <T> Builder addParameter(EventKey<T> key, T value) {
            this.map.put(key, value);
            return this;
        }

        @Override
        public EventContextImpl build() {
            return new EventContextImpl(type, map);
        }
    }
}
