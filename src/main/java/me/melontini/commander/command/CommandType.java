package me.melontini.commander.command;

import com.mojang.serialization.Codec;
import me.melontini.commander.data.types.CommandTypes;
import net.minecraft.util.Identifier;

public interface CommandType {

    static CommandType register(Identifier identifier, Codec<? extends Command> codec) {
        return CommandTypes.register(identifier, () -> codec);
    }

    Codec<? extends Command> codec();
}
