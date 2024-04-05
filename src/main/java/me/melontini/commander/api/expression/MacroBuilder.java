package me.melontini.commander.api.expression;

import com.google.common.collect.ImmutableMap;
import me.melontini.commander.api.util.functions.ToDoubleBiFunction;
import me.melontini.commander.api.util.functions.ToDoubleFunction;
import me.melontini.commander.impl.util.macro.MacroContainer;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MacroBuilder {
    private final Map<String, ToDoubleFunction<LootContext>> arithmeticFunctions = new HashMap<>();
    private final Map<String, Function<LootContext, String>> stringFunctions = new HashMap<>();
    private final Map<String, MacroContainer.ArithmeticEntry<?>> dynamicArithmeticFunctions = new HashMap<>();
    private final Map<String, MacroContainer.StringEntry<?>> dynamicStringFunctions = new HashMap<>();

    public static Consumer<MacroBuilder> empty() {
        return builder -> {};
    }

    public static Consumer<MacroBuilder> forEntity(Function<LootContext, ? extends Entity> key) {
        return builder -> builder
                .string("uuid", context -> entity(key, context).getUuidAsString())
                .string("key", context -> Registries.ENTITY_TYPE.getId(entity(key, context).getType()).toString())
                .arithmetic("x", context -> entity(key, context).getX())
                .arithmetic("y", context -> entity(key, context).getY())
                .arithmetic("z", context -> entity(key, context).getZ())
                .arithmetic("rot/x", context -> entity(key, context).getPitch())
                .arithmetic("rot/y", context -> entity(key, context).getYaw())
                .arithmetic("vel/x", context -> entity(key, context).getVelocity().x)
                .arithmetic("vel/y", context -> entity(key, context).getVelocity().y)
                .arithmetic("vel/z", context -> entity(key, context).getVelocity().z)
                .arithmetic("age", context -> entity(key, context).age)
                .arithmetic("living/health", context -> living(key, context).getHealth())
                .arithmetic("living/max_health", context -> living(key, context).getMaxHealth())
                .arithmetic("living/stuck_arrows", context -> living(key, context).getStuckArrowCount())
                .arithmetic("living/stingers", context -> living(key, context).getStingerCount())
                .arithmetic("living/armor", context -> living(key, context).getArmor())
                .dynamicArithmetic("living/attribute", string -> Registries.ATTRIBUTE.get(Identifier.tryParse(string)), (attribute, context) -> living(key, context).getAttributeValue(attribute))
                .arithmetic("player/xp/level", context -> player(key, context).experienceLevel)
                .arithmetic("player/xp/total", context -> player(key, context).totalExperience);
    }

    public static Consumer<MacroBuilder> forEntity(Function<LootContext, ? extends Entity> key, Consumer<MacroBuilder> consumer) {
        return forEntity(key).andThen(consumer);
    }

    private static LivingEntity living(Function<LootContext, ? extends Entity> key, LootContext source) {
        return (LivingEntity) entity(key, source);
    }

    private static ServerPlayerEntity player(Function<LootContext, ? extends Entity> key, LootContext source) {
        return (ServerPlayerEntity) living(key, source);
    }

    private static Entity entity(Function<LootContext, ? extends Entity> key, LootContext source) {
        return Objects.requireNonNull(key.apply(source), "missing required context!");
    }

    public MacroBuilder arithmetic(String field, ToDoubleFunction<LootContext> function) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        arithmeticFunctions.put(field, function);
        return this;
    }

    public MacroBuilder string(String field, Function<LootContext, String> function) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        stringFunctions.put(field, function);
        return this;
    }

    public <T> MacroBuilder dynamicArithmetic(String field, Function<String, T> transformer, ToDoubleBiFunction<T, LootContext> arithmetic) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        dynamicArithmeticFunctions.put(field, new MacroContainer.ArithmeticEntry<>(transformer, arithmetic));
        return this;
    }

    public <T> MacroBuilder dynamicString(String field, Function<String, T> transformer, BiFunction<T, LootContext, String> arithmetic) {
        if (isDuplicate(field)) throw new IllegalStateException("Tried to register field '%s' twice!".formatted(field));
        dynamicStringFunctions.put(field, new MacroContainer.StringEntry<>(transformer, arithmetic));
        return this;
    }

    private boolean isDuplicate(String field) {
        return arithmeticFunctions.containsKey(field) || stringFunctions.containsKey(field)
                || dynamicArithmeticFunctions.containsKey(field) || dynamicStringFunctions.containsKey(field);
    }

    public MacroBuilder merge(String prefix, Consumer<MacroBuilder> consumer) {
        MacroBuilder other = new MacroBuilder();
        consumer.accept(other);

        other.arithmeticFunctions.forEach((s, f) -> this.arithmetic(prefix + "/" + s, f));
        other.stringFunctions.forEach((s, f) -> this.string(prefix + "/" + s, f));

        other.dynamicArithmeticFunctions.forEach((s, f) -> this.dynamicArithmetic(prefix + "/" + s, Utilities.cast(f.transformer()), f.arithmetic()));
        other.dynamicStringFunctions.forEach((s, f) -> this.dynamicString(prefix + "/" + s, Utilities.cast(f.transformer()), f.string()));

        return this;
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
