package me.melontini.commander.command.selector;

import com.google.common.collect.ImmutableMap;
import me.melontini.commander.util.functions.ToDoubleFunction;
import me.melontini.commander.util.macro.MacroContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class MacroBuilder {
    private final Map<String, ToDoubleFunction<ServerCommandSource>> arithmeticFunctions = new HashMap<>();
    private final Map<String, Function<ServerCommandSource, String>> stringFunctions = new HashMap<>();

    public static Consumer<MacroBuilder> forEntity() {
        return builder -> builder
                .string("uuid", source -> entity(source).getUuidAsString())
                .arithmetic("vel/x", source -> entity(source).getVelocity().x)
                .arithmetic("vel/y", source -> entity(source).getVelocity().y)
                .arithmetic("vel/z", source -> entity(source).getVelocity().z)
                .arithmetic("age", source -> entity(source).age)
                .arithmetic("living/health", source -> living(source).getHealth())
                .arithmetic("living/max_health", source -> living(source).getMaxHealth())
                .arithmetic("living/stuck_arrows", source -> living(source).getStuckArrowCount())
                .arithmetic("living/stingers", source -> living(source).getStingerCount())
                .arithmetic("living/armor", source -> living(source).getArmor())
                .arithmetic("player/xp/level", source -> player(source).experienceLevel)
                .arithmetic("player/xp/total", source -> player(source).totalExperience);
    }

    public static Consumer<MacroBuilder> forEntity(Consumer<MacroBuilder> consumer) {
        return forEntity().andThen(consumer);
    }

    private static LivingEntity living(ServerCommandSource source) {
        return ((LivingEntity) Objects.requireNonNull(source.getEntity()));
    }

    private static ServerPlayerEntity player(ServerCommandSource source) {
        return Objects.requireNonNull(source.getPlayer());
    }

    private static Entity entity(ServerCommandSource source) {
        return Objects.requireNonNull(source.getEntity());
    }

    public MacroBuilder arithmetic(String field, ToDoubleFunction<ServerCommandSource> function) {
        var old = arithmeticFunctions.put(field, function);
        if (old != null || stringFunctions.containsKey(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        return this;
    }

    public MacroBuilder string(String field, Function<ServerCommandSource, String> function) {
        var old = stringFunctions.put(field, function);
        if (old != null || arithmeticFunctions.containsKey(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        return this;
    }

    public MacroContainer build() {
        return new MacroContainer(
                ImmutableMap.copyOf(arithmeticFunctions),
                ImmutableMap.copyOf(stringFunctions)
        );
    }
}
