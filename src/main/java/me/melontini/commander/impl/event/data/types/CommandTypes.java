package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;

import java.util.Objects;

@UtilityClass
public final class CommandTypes {

    private static final BiMap<Identifier, CommandType> COMMANDS = HashBiMap.create();
    private static final Codec<CommandType> TYPE_CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, COMMANDS);
    public static final Codec<Command> CODEC = TYPE_CODEC.dispatch("type", Command::type, CommandType::codec);

    public static Identifier getId(CommandType type) {
        return Objects.requireNonNull(COMMANDS.inverse().get(type), () -> "Unregistered CommandType %s!".formatted(type));
    }

    public static CommandType getType(Identifier identifier) {
        return Objects.requireNonNull(COMMANDS.get(identifier), () -> "Unknown CommandType %s!".formatted(identifier));
    }

    public static CommandType register(Identifier identifier, CommandType type) {
        var old = COMMANDS.put(identifier, type);
        if (old != null) throw new IllegalStateException("Already registered command %s".formatted(identifier));
        return type;
    }
}
