package dev.zenfyr.commander.impl.mixin;

import static dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader.NO_EXPRESSION_EXCEPTION;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.pulsar.api.loot.LootContextBuilder;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScoreboardCommand.class)
public class ScoreboardCommandMixin {

  @ModifyExpressionValue(
      method = "register",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/commands/Commands;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;",
              ordinal = 8))
  private static LiteralArgumentBuilder<CommandSourceStack> addCommanderOperator(
      LiteralArgumentBuilder<CommandSourceStack> original) {
    return original
        .then(Commands.literal("cmd:expression")
            .then(Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                .then(Commands.argument("objective", ObjectiveArgument.objective())
                    .then(Commands.argument("expression", StringArgumentType.string())
                        .executes(context -> {
                          var r =
                              Expression.parse(StringArgumentType.getString(context, "expression"));
                          if (r.error().isPresent())
                            throw Commander.EXPRESSION_EXCEPTION.create(
                                r.error().get().message());
                          return cmd$evaluateExpression(r.result().orElseThrow(), context);
                        })))))
        .then(Commands.literal("cmd:library")
            .then(Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                .then(Commands.argument("objective", ObjectiveArgument.objective())
                    .then(Commands.argument("expression", ResourceLocationArgument.id())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                            ExpressionLibrary.get(context.getSource().getServer())
                                .allExpressions()
                                .keySet(),
                            builder))
                        .executes(context -> {
                          var identifier = ResourceLocationArgument.getId(context, "expression");
                          var expression = ExpressionLibrary.get(
                                  context.getSource().getServer())
                              .getExpression(identifier);
                          if (expression == null) throw NO_EXPRESSION_EXCEPTION.create(identifier);
                          return cmd$evaluateExpression(expression, context);
                        })))));
  }

  @Unique private static int cmd$evaluateExpression(
      Expression expression, CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    var targets = new ArrayList<>(ScoreHolderArgument.getNames(context, "targets"));
    var objective = ObjectiveArgument.getWritableObjective(context, "objective");

    Scoreboard scoreboard = context.getSource().getServer().getScoreboard();

    LootContext context1 = LootContextBuilder.command(
        context.getSource().getLevel(),
        builder -> builder.origin(context.getSource().getPosition()));

    for (String target : targets) {
      Score score = scoreboard.getOrCreatePlayerScore(target, objective);
      int result = expression
          .eval(
              context1,
              Collections.singletonMap("score", Expression.Result.convert(score.getScore())))
          .getAsInt();
      score.setScore(result);
    }

    if (targets.size() == 1) {
      context
          .getSource()
          .sendSuccess(
              () -> Component.translatable(
                  "commands.scoreboard.players.set.success.single",
                  objective.getDisplayName(),
                  targets.get(0),
                  expression.original()),
              true);
    } else {
      context
          .getSource()
          .sendSuccess(
              () -> Component.translatable(
                  "commands.scoreboard.players.set.success.multiple",
                  objective.getDisplayName(),
                  targets.size(),
                  expression.original()),
              true);
    }

    return targets.size();
  }
}
