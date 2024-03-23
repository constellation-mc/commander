package me.melontini.commander.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Accessors;
import me.melontini.commander.util.DataType;
import net.minecraft.loot.context.LootContext;

import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class EventContext {
    @Getter
    @With
    private final LootContext lootContext;
    @Getter
    private final EventType type;
    private final AtomicReference<Object> returnValue;

    public EventContext(LootContext lootContext, EventType type) {
        this.lootContext = lootContext;
        this.type = type;
        this.returnValue = new AtomicReference<>();
    }

    public void setReturnValue(Object value) {
        returnValue.set(value);
    }

    public <T> T getReturnValue(DataType<T> type, T def) {
        var r = returnValue.get();
        if (r == null) return def;
        return (T) r;
    }
}
