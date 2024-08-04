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

  /**
   * Executes the command with the provided event context.
   * @param context {@link EventContext}
   * @return If the execution was successful.
   */
  boolean execute(EventContext context);

  /**
   * @return The registered command type.
   * @see CommandType#register(Identifier, MapCodec)
   */
  CommandType type();

  /**
   * Validates that a command can used with the event type.
   * Internally this is only used for the cancel command.
   * @param type The {@link EventType} expected to for this command.
   * @return {@link DataResult}.
   */
  default DataResult<Void> validate(EventType type) {
    return DataResult.success(null);
  }

  /**
   * Executable command proxy. This interface is to be used when you nest additional commands in your base command.
   */
  interface Conditioned {
    /**
     * @see Command#execute(EventContext)
     */
    boolean execute(EventContext context);

    /**
     * @see Command#validate(EventType)
     */
    DataResult<Void> validate(EventType type);
  }
}
