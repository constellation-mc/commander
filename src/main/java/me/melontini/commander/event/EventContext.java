package me.melontini.commander.event;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.commander.util.DataType;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Accessors(fluent = true)
public final class EventContext {

    @Getter
    private final EventType type;
    private final Map<EventKey<?>, Object> parameters;

    private EventContext(EventType type, Map<EventKey<?>, Object> parameters) {
        this.parameters = parameters;
        this.type = type;
    }

    public EventContext with(Map<EventKey<?>, Object> parameters) {
        var map = new IdentityHashMap<>(this.parameters);
        map.putAll(parameters);
        return new EventContext(type, map);
    }

    public <T> @NotNull T getParameter(EventKey<T> key) {
        var r = parameters.get(key);
        if (r == null) throw new IllegalStateException("Missing required parameter key %s".formatted(key.id()));
        return (T) r;
    }

    public @NotNull LootContext lootContext() {
        return getParameter(EventKey.LOOT_CONTEXT);
    }

    public void setReturnValue(Object value) {
        getParameter(EventKey.RETURN_VALUE).set(value);
    }

    public <T> T getReturnValue(DataType<T> type, T def) {
        var r = getParameter(EventKey.RETURN_VALUE).get();
        if (r == null) return def;
        return (T) r;
    }

    public static Builder builder(EventType type) {
        return new Builder(type);
    }

    public static final class Builder {
        private final Map<EventKey<?>, Object> map = new IdentityHashMap<>();
        private final EventType type;

        public Builder(EventType type) {
            this.type = type;

            if (this.type.context().get(EventType.CANCEL_TERM).isPresent()) {
                map.put(EventKey.RETURN_VALUE, new AtomicReference<>());
            }
        }

        public <T> Builder addParameter(EventKey<T> key, T value) {
            this.map.put(key, value);
            return this;
        }

        public EventContext build() {
            return new EventContext(type, map);
        }
    }
}
