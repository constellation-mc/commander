package me.melontini.commander.api.command;

import com.mojang.serialization.MapCodec;
import me.melontini.commander.impl.event.data.types.CommandTypes;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface CommandType {

  /**
   * Registers a command to be used in events.
   * @param identifier The command identifier.
   * @param codec The codec to decode the command from JSON.
   * @return The command type to be returned in {@link Command#type()}
   * @see Command#type()
   */
  static CommandType register(Identifier identifier, MapCodec<? extends Command> codec) {
    return CommandTypes.register(identifier, () -> codec);
  }

  MapCodec<? extends Command> codec();
}
