package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Either;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.util.eval.CmdEvalException;
import me.melontini.commander.impl.util.eval.EvalUtils;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ArithmeticaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cmd:arithmetica")
                .then(CommandManager.argument("expression", StringArgumentType.string())
                        .executes(context -> {
                            try {
                                String expression = StringArgumentType.getString(context, "expression");

                                var r = EvalUtils.parseEither(Either.right(expression));
                                if (r.error().isPresent()) {
                                    context.getSource().sendError(Text.literal(r.error().get().message()));
                                    return 0;
                                }
                                LootContext context1 = new LootContext.Builder(new LootContextParameterSet.Builder(context.getSource().getWorld())
                                        .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                                        .addOptional(LootContextParameters.THIS_ENTITY, context.getSource().getEntity())
                                        .build(LootContextTypes.COMMAND)).build(null);

                                context.getSource().sendMessage(Text.literal(String.valueOf(r.result().orElseThrow().asDouble(context1))));
                                return 1;
                            } catch (CmdEvalException e) {
                                context.getSource().sendError(Text.literal(e.getMessage()));
                                return 0;
                            } catch (Throwable t) {
                                Commander.LOGGER.error(t);
                                throw t;
                            }
                        })));
    }
}
