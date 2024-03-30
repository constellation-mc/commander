package me.melontini.commander.command.selector;

import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Extractor extends Function<ServerCommandSource, String> {

    static Consumer<Map<String, Extractor>> forEntity() {
        return map -> {
            map.put("uuid", source -> Objects.requireNonNull(source.getEntity()).getUuidAsString());
        };
    }

    static Consumer<Map<String, Extractor>> forEntity(Consumer<Map<String, Extractor>> consumer) {
        return forEntity().andThen(consumer);
    }

    @Override
    String apply(ServerCommandSource source);
}
