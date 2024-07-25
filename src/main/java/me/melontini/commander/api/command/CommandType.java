package me.melontini.commander.api.command;

import com.mojang.serialization.MapCodec;
import me.melontini.commander.impl.event.data.types.CommandTypes;
import net.minecraft.util.Identifier;

public interface CommandType {

  static CommandType register(Identifier identifier, MapCodec<? extends Command> codec) {
    return CommandTypes.register(identifier, () -> codec);
  }

  MapCodec<? extends Command> codec();
}
