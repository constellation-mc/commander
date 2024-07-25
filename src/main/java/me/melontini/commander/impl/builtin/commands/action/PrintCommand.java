package me.melontini.commander.impl.builtin.commands.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;

public record PrintCommand(String text) implements Command {

  public static final MapCodec<PrintCommand> CODEC =
      Codec.STRING.fieldOf("text").xmap(PrintCommand::new, PrintCommand::text);

  @Override
  public boolean execute(EventContext context) {
    System.out.println(text());
    return true;
  }

  @Override
  public CommandType type() {
    return BuiltInCommands.PRINT;
  }
}
