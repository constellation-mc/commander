package me.melontini.commander.api.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.melontini.commander.impl.Commander;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class EventKey<T> {

    public static final EventKey<LootContext> LOOT_CONTEXT = create(Commander.id("loot_context"));
    public static final EventKey<AtomicReference<Object>> RETURN_VALUE = create(Commander.id("return_value"));

    private final Identifier id;

    @Contract("_ -> new")
    public static <T> @NotNull EventKey<T> create(Identifier id) {
        return new EventKey<>(id);
    }
}
