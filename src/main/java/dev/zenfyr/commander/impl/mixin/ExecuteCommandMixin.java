package dev.zenfyr.commander.impl.mixin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader;
import dev.zenfyr.pulsar.loot.LootContextBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

  @Shadow
  private static ArgumentBuilder<CommandSourceStack, ?> addConditional(
      CommandNode<CommandSourceStack> root,
      ArgumentBuilder<CommandSourceStack, ?> builder,
      boolean positive,
      ExecuteCommand.CommandPredicate condition) {
    throw new AssertionError();
  }

  @Inject(
      at =
          @At(
              value = "FIELD",
              target =
                  "Lnet/minecraft/server/commands/data/DataCommands;SOURCE_PROVIDERS:Ljava/util/List;",
              shift = At.Shift.BEFORE),
      method = "addConditionals")
  private static void commander$addExpressionCondition(
      CommandNode<CommandSourceStack> root,
      LiteralArgumentBuilder<CommandSourceStack> argumentBuilder,
      boolean positive,
      CommandBuildContext commandRegistryAccess,
      CallbackInfoReturnable<ArgumentBuilder<CommandSourceStack, ?>> cir) {
    argumentBuilder
        .then(Commands.literal("cmd:expression")
            .then(addConditional(
                root,
                Commands.argument("expression", StringArgumentType.string()),
                positive,
                context -> {
                  var r = Expression.parse(StringArgumentType.getString(context, "expression"));
                  if (r.error().isPresent())
                    throw Commander.EXPRESSION_EXCEPTION.create(r.error().get().message());
                  return cmd$evaluateExpression(r.result().orElseThrow(), context);
                })))
        .then(Commands.literal("cmd:library")
            .then(addConditional(
                root,
                Commands.argument("expression", ResourceLocationArgument.id())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                        ExpressionLibrary.get(context.getSource().getServer())
                            .allExpressions()
                            .keySet(),
                        builder)),
                positive,
                context -> {
                  var identifier = ResourceLocationArgument.getId(context, "expression");
                  var expression = ExpressionLibrary.get(context.getSource().getServer())
                      .getExpression(identifier);
                  if (expression == null)
                    throw ExpressionLibraryLoader.NO_EXPRESSION_EXCEPTION.create(identifier);
                  return cmd$evaluateExpression(expression, context);
                })));
  }

  @Unique private static boolean cmd$evaluateExpression(
      Expression expression, CommandContext<CommandSourceStack> context) {
    return expression
        .eval(LootContextBuilder.command(
            context.getSource().getLevel(),
            builder -> builder
                .origin(context.getSource().getPosition())
                .thisEntity(context.getSource().getEntity())))
        .getAsBoolean();
  }
}
