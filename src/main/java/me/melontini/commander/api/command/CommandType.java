package me.melontini.commander.api.command;

import com.mojang.serialization.Codec;
import me.melontini.commander.impl.event.data.types.CommandTypes;
import net.minecraft.util.Identifier;

public interface CommandType {

    static CommandType register(Identifier identifier, Codec<? extends Command> codec) {
        return CommandTypes.register(identifier, () -> codec);
    }

    Codec<? extends Command> codec();
}
