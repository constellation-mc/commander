package me.melontini.commander.api.command.selector;

import com.google.common.collect.ImmutableMap;
import me.melontini.commander.api.util.functions.ToDoubleBiFunction;
import me.melontini.commander.api.util.functions.ToDoubleFunction;
import me.melontini.commander.impl.util.macro.MacroContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MacroBuilder {
    private final Map<String, ToDoubleFunction<ServerCommandSource>> arithmeticFunctions = new HashMap<>();
    private final Map<String, Function<ServerCommandSource, String>> stringFunctions = new HashMap<>();
    private final Map<String, MacroContainer.ArithmeticEntry<?>> dynamicArithmeticFunctions = new HashMap<>();
    private final Map<String, MacroContainer.StringEntry<?>> dynamicStringFunctions = new HashMap<>();

    public static Consumer<MacroBuilder> forEntity() {
        return builder -> builder
                .string("uuid", source -> entity(source).getUuidAsString())
                .string("key", source -> Registries.ENTITY_TYPE.getId(entity(source).getType()).toString())
                .arithmetic("vel/x", source -> entity(source).getVelocity().x)
                .arithmetic("vel/y", source -> entity(source).getVelocity().y)
                .arithmetic("vel/z", source -> entity(source).getVelocity().z)
                .arithmetic("age", source -> entity(source).age)
                .arithmetic("living/health", source -> living(source).getHealth())
                .arithmetic("living/max_health", source -> living(source).getMaxHealth())
                .arithmetic("living/stuck_arrows", source -> living(source).getStuckArrowCount())
                .arithmetic("living/stingers", source -> living(source).getStingerCount())
                .arithmetic("living/armor", source -> living(source).getArmor())
                .dynamicArithmetic("living/attribute", string -> Registries.ATTRIBUTE.get(Identifier.tryParse(string)), (attribute, source) -> living(source).getAttributeValue(attribute))
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
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        arithmeticFunctions.put(field, function);
        return this;
    }

    public MacroBuilder string(String field, Function<ServerCommandSource, String> function) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        stringFunctions.put(field, function);
        return this;
    }

    public <T> MacroBuilder dynamicArithmetic(String field, Function<String, T> transformer, ToDoubleBiFunction<T, ServerCommandSource> arithmetic) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        dynamicArithmeticFunctions.put(field, new MacroContainer.ArithmeticEntry<>(transformer, arithmetic));
        return this;
    }

    public <T> MacroBuilder dynamicString(String field, Function<String, T> transformer, BiFunction<T, ServerCommandSource, String> arithmetic) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        dynamicStringFunctions.put(field, new MacroContainer.StringEntry<>(transformer, arithmetic));
        return this;
    }

    private boolean isDuplicate(String field) {
        return arithmeticFunctions.containsKey(field) || stringFunctions.containsKey(field)
                || dynamicArithmeticFunctions.containsKey(field) || dynamicStringFunctions.containsKey(field);
    }

    public MacroContainer build() {
        return new MacroContainer(
                ImmutableMap.copyOf(arithmeticFunctions),
                ImmutableMap.copyOf(stringFunctions),
                ImmutableMap.copyOf(dynamicArithmeticFunctions),
                ImmutableMap.copyOf(dynamicStringFunctions)
        );
    }
}
