package me.melontini.commander.command.selector;

import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Extractor extends Function<ServerCommandSource, String> {

    static Consumer<MacroBuilder> forEntity() {
        return builder -> builder
                .string("uuid", source -> Objects.requireNonNull(source.getEntity()).getUuidAsString())
                .arithmetic("vel/x", source -> Objects.requireNonNull(source.getEntity()).getVelocity().x)
                .arithmetic("vel/y", source -> Objects.requireNonNull(source.getEntity()).getVelocity().y)
                .arithmetic("vel/z", source -> Objects.requireNonNull(source.getEntity()).getVelocity().z);
    }

    static Consumer<MacroBuilder> forEntity(Consumer<MacroBuilder> consumer) {
        return forEntity().andThen(consumer);
    }

    @Override
    String apply(ServerCommandSource source);
}
