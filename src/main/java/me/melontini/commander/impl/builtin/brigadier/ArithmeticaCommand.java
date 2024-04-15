package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.util.eval.CmdEvalException;
import me.melontini.commander.impl.util.macro.PatternParser;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Set;

public class ArithmeticaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        Set<String> casts = Set.of("long", "int", "bool", "double");
        var cmd = CommandManager.argument("expression", StringArgumentType.string()).executes(context -> execute(context, StringArgumentType.getString(context, "expression"), null));

        for (String cast : casts) {
            cmd.then(CommandManager.literal(cast).executes(context -> execute(context, StringArgumentType.getString(context, "expression"), cast)));
        }

        dispatcher.register(CommandManager.literal("cmd:arithmetica").then(cmd));
    }

    private static int execute(CommandContext<ServerCommandSource> context, String expression, String cast) {
        try {
            var r = PatternParser.parseExpression(expression, cast);
            if (r.error().isPresent()) {
                context.getSource().sendError(Text.literal(r.error().get().message()));
                return 0;
            }
            LootContext context1 = new LootContext.Builder(new LootContextParameterSet.Builder(context.getSource().getWorld())
                    .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                    .addOptional(LootContextParameters.THIS_ENTITY, context.getSource().getEntity())
                    .build(LootContextTypes.COMMAND)).build(null);

            context.getSource().sendMessage(Text.literal(r.result().orElseThrow().apply(context1)));
            return 1;
        } catch (CmdEvalException e) {
            context.getSource().sendError(Text.literal(e.getMessage()));
            return 0;
        } catch (Throwable t) {
            Commander.LOGGER.error(t);
            throw t;
        }
    }
}
