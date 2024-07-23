package me.melontini.commander.impl.builtin.commands.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.util.ServerHelper;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record CommandCommand(Selector.Conditioned selector, InterFunction commands)
    implements Command {

  public static final MapCodec<CommandCommand> CODEC =
      RecordCodecBuilder.mapCodec(data -> data.group(
              Selector.CODEC.fieldOf("selector").forGetter(CommandCommand::selector),
              ExtraCodecs.either(BrigadierMacro.CODEC.listOf(), Identifier.CODEC)
                  .<InterFunction>xmap(
                      e -> e.map(MacroedFunction::new, FunctionFunction::new),
                      InterFunction::origin)
                  .fieldOf("commands")
                  .forGetter(CommandCommand::commands))
          .apply(data, CommandCommand::new));

  @Override
  public boolean execute(EventContext context) {
    var opt = selector().select(context).map(ServerCommandSource::withSilent);
    if (opt.isEmpty()) return false;
    return commands().execute(context.lootContext(), opt.orElseThrow());
  }

  @Override
  public CommandType type() {
    return BuiltInCommands.COMMANDS;
  }

  private interface InterFunction {
    boolean execute(LootContext context, ServerCommandSource source);

    Either<List<BrigadierMacro>, Identifier> origin();
  }

  private record MacroedFunction(List<BrigadierMacro> macros) implements InterFunction {

    @Override
    public boolean execute(LootContext context, ServerCommandSource source) {
      var server = context.getWorld().getServer();

      for (BrigadierMacro command : macros()) {
        try {
          server.getCommandManager().executeWithPrefix(source, command.build(context));
        } catch (Throwable e) {
          ServerHelper.broadcastToOps(
              server,
              Text.literal(command.original())
                  .append(Text.literal(" failed execution! "))
                  .append("%s: %s".formatted(e.getClass().getSimpleName(), e.getLocalizedMessage()))
                  .formatted(Formatting.RED));
          return false;
        }
      }
      return true;
    }

    @Override
    public Either<List<BrigadierMacro>, Identifier> origin() {
      return Either.left(macros());
    }
  }

  private record FunctionFunction(Identifier identifier) implements InterFunction {

    @Override
    public boolean execute(LootContext context, ServerCommandSource source) {
      var server = context.getWorld().getServer();

      var func = server.getCommandFunctionManager().getFunction(identifier());
      if (func.isPresent()) {
        server.getCommandFunctionManager().execute(func.orElseThrow(), source);
        return true;
      } else {
        ServerHelper.broadcastToOps(
            server,
            Text.literal("Unknown function %s!".formatted(identifier())).formatted(Formatting.RED));
        return false;
      }
    }

    @Override
    public Either<List<BrigadierMacro>, Identifier> origin() {
      return Either.right(identifier());
    }
  }
}
