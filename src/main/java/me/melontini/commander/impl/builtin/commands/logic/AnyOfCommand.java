package me.melontini.commander.impl.builtin.commands.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

public record AnyOfCommand(
    List<Command.Conditioned> commands, Optional<Command.Conditioned> then, boolean shortCircuit)
    implements Command {

  public static final MapCodec<AnyOfCommand> CODEC = RecordCodecBuilder.mapCodec(data -> data.group(
          ExtraCodecs.list(Command.CODEC.codec())
              .fieldOf("commands")
              .forGetter(AnyOfCommand::commands),
          ExtraCodecs.optional("then", Command.CODEC.codec()).forGetter(AnyOfCommand::then),
          ExtraCodecs.optional("short_circuit", Codec.BOOL, false)
              .forGetter(AnyOfCommand::shortCircuit))
      .apply(data, AnyOfCommand::new));

  @Override
  public boolean execute(EventContext context) {
    if (!shortCircuit) {
      boolean b = false;
      for (Command.Conditioned command : commands()) {
        b |= command.execute(context);
      }
      if (b) {
        return then().map(cc -> cc.execute(context)).orElse(true);
      }
      return false;
    }

    for (Command.Conditioned command : commands()) {
      if (command.execute(context)) return then().map(cc -> cc.execute(context)).orElse(true);
    }
    return false;
  }

  @Override
  public CommandType type() {
    return BuiltInCommands.ANY_OF;
  }
}
