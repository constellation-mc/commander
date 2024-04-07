package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Either;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.util.ExpressionParser;
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

                                var r = ExpressionParser.parseEither(Either.right(expression));
                                if (r.error().isPresent()) {
                                    context.getSource().sendError(Text.literal(r.error().get().message()));
                                    return 0;
                                }
                                var eq = r.result().orElseThrow();
                                if (eq.toSource().left().isPresent()) {
                                    context.getSource().sendMessage(Text.literal(String.valueOf(eq.asDouble(null))));
                                    return 1;
                                }
                                LootContext context1 = new LootContext.Builder(new LootContextParameterSet.Builder(context.getSource().getWorld())
                                        .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                                        .addOptional(LootContextParameters.THIS_ENTITY, context.getSource().getEntity())
                                        .build(LootContextTypes.COMMAND)).build(null);

                                context.getSource().sendMessage(Text.literal(String.valueOf(eq.asDouble(context1))));
                                return 1;
                            } catch (Throwable t) {
                                Commander.LOGGER.error(t);
                                throw t;
                            }
                        })));
    }
}
