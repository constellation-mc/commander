package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.CmdEvalException;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.macro.PatternParser;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class ArithmeticaCommand {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    var cmd = CommandManager.argument("expression", StringArgumentType.string())
        .executes(
            context -> execute(context, StringArgumentType.getString(context, "expression"), null));

    for (String cast : PatternParser.CONVERTERS.keySet()) {
      cmd.then(CommandManager.literal(cast)
          .executes(context ->
              execute(context, StringArgumentType.getString(context, "expression"), cast)));
    }

    dispatcher.register(CommandManager.literal("cmd:arithmetica")
        .requires(source -> source.hasPermissionLevel(2))
        .then(cmd));
  }

  private static int execute(
      CommandContext<ServerCommandSource> context, String expression, @Nullable String cast)
      throws CommandSyntaxException {
    try {
      var r = PatternParser.parseExpression(expression, cast);
      if (r.error().isPresent())
        throw Commander.EXPRESSION_EXCEPTION.create(r.error().get().message());

      LootContext context1 = new LootContext.Builder(
              new LootContextParameterSet.Builder(context.getSource().getWorld())
                  .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                  .addOptional(
                      LootContextParameters.THIS_ENTITY, context.getSource().getEntity())
                  .build(LootContextTypes.COMMAND))
          .build(Optional.empty());

      context
          .getSource()
          .sendMessage(Text.literal(
              EvalUtils.prettyToString(r.result().orElseThrow().apply(context1, null))));
      return 1;
    } catch (CmdEvalException e) {
      throw Commander.EXPRESSION_EXCEPTION.create(e.getMessage());
    }
  }
}
