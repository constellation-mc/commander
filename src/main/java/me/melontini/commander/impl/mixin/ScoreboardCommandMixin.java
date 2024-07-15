package me.melontini.commander.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(ScoreboardCommand.class)
public class ScoreboardCommandMixin {

    @ModifyExpressionValue(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", ordinal = 8))
    private static LiteralArgumentBuilder<ServerCommandSource> addCommanderOperator(LiteralArgumentBuilder<ServerCommandSource> original) {
        return original.then(CommandManager.literal("cmd:operate")
                .then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
                        .then(CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                                .then(CommandManager.argument("expression", StringArgumentType.string()).executes(context -> {
                                    var targets = new ArrayList<>(ScoreHolderArgumentType.getScoreboardScoreHolders(context, "targets"));
                                    var objective = ScoreboardObjectiveArgumentType.getWritableObjective(context, "objective");

                                    var r = EvalUtils.parseExpression(StringArgumentType.getString(context, "expression"));
                                    if (r.error().isPresent()) throw Commander.EXPRESSION_EXCEPTION.create(r.error().orElseThrow().message());
                                    var expression = r.result().orElseThrow();

                                    Scoreboard scoreboard = context.getSource().getServer().getScoreboard();

                                    LootContext context1 = new LootContext.Builder(new LootContextParameterSet.Builder(context.getSource().getWorld())
                                            .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                                            .build(LootContextTypes.COMMAND)).build(null);

                                    for (String target : targets) {
                                        ScoreboardPlayerScore score = scoreboard.getPlayerScore(target, objective);
                                        score.setScore(EvalUtils.evaluate(context1, expression.with("score", score.getScore())).getNumberValue().intValue());
                                    }

                                    if (targets.size() == 1) {
                                        context.getSource().sendFeedback(() -> Text.translatable("commands.scoreboard.players.set.success.single", objective.toHoverableText(), targets.get(0), r.result().orElseThrow().getExpressionString()), true);
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.translatable("commands.scoreboard.players.set.success.multiple", objective.toHoverableText(), targets.size(), r.result().orElseThrow().getExpressionString()), true);
                                    }

                                    return targets.size();
                                })))));
    }
}
