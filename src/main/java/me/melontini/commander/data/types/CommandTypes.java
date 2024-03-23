package me.melontini.commander.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.util.MagicCodecs;
import net.minecraft.util.Identifier;

@UtilityClass
public final class CommandTypes {

    private static final BiMap<Identifier, CommandType> COMMANDS = HashBiMap.create();
    private static final Codec<CommandType> TYPE_CODEC = MagicCodecs.mapLookup(COMMANDS);
    public static final Codec<Command> CODEC = TYPE_CODEC.dispatch("type", Command::type, CommandType::codec);

    public static Identifier getId(CommandType type) {
        return COMMANDS.inverse().get(type);
    }

    public static CommandType getType(Identifier identifier) {
        return COMMANDS.get(identifier);
    }

    public static CommandType register(Identifier identifier, Codec<? extends Command> codec) {
        CommandType type = new CommandType(codec);
        var old = COMMANDS.put(identifier, type);
        if (old != null) throw new IllegalStateException("Already registered command %s".formatted(identifier));
        return type;
    }
}
