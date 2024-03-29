package me.melontini.commander.event;

import me.melontini.commander.Commander;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicReference;

public record EventKey<T>(Identifier id) {

    public static final EventKey<LootContext> LOOT_CONTEXT = new EventKey<>(Commander.id("loot_context"));
    public static final EventKey<AtomicReference<Object>> RETURN_VALUE = new EventKey<>(Commander.id("return_value"));

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
