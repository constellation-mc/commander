package me.melontini.commander.command.selector;

import com.google.common.collect.ImmutableMap;
import me.melontini.commander.util.functions.ToDoubleFunction;
import me.melontini.commander.util.macro.MacroContainer;
import net.minecraft.server.command.ServerCommandSource;

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
                .string("uuid", source -> Objects.requireNonNull(source.getEntity()).getUuidAsString())
                .arithmetic("vel/x", source -> Objects.requireNonNull(source.getEntity()).getVelocity().x)
                .arithmetic("vel/y", source -> Objects.requireNonNull(source.getEntity()).getVelocity().y)
                .arithmetic("vel/z", source -> Objects.requireNonNull(source.getEntity()).getVelocity().z);
    }

    public static Consumer<MacroBuilder> forEntity(Consumer<MacroBuilder> consumer) {
        return forEntity().andThen(consumer);
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
