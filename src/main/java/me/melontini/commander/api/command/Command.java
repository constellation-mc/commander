package me.melontini.commander.api.command;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.impl.command.ConditionedCommand;
import net.minecraft.util.Identifier;

/**
 * Base command interface to be implemented on your command classes.
 * <p>Commands along with their {@link Codec} must be registered with {@link CommandType#register(Identifier, MapCodec)}.</p>
 */
public interface Command {

  MapCodec<Conditioned> CODEC = (MapCodec<Conditioned>) ConditionedCommand.CODEC;

  boolean execute(EventContext context);

  CommandType type();

  default DataResult<Void> validate(EventType type) {
    return DataResult.success(null);
  }

  /**
   * Executable command proxy. This interface is to be used when you nest additional commands in your base command.
   */
  interface Conditioned {
    boolean execute(EventContext context);

    DataResult<Void> validate(EventType type);
  }
}
