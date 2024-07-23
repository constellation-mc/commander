package me.melontini.commander.impl.mixin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Optional;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.impl.Commander;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {

  @Shadow
  private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(
      CommandNode<ServerCommandSource> root,
      ArgumentBuilder<ServerCommandSource, ?> builder,
      boolean positive,
      ExecuteCommand.Condition condition) {
    throw new AssertionError();
  }

  @Inject(
      at =
          @At(
              value = "FIELD",
              target =
                  "Lnet/minecraft/server/command/DataCommand;SOURCE_OBJECT_TYPES:Ljava/util/List;",
              shift = At.Shift.BEFORE),
      method = "addConditionArguments")
  private static void commander$addExpressionCondition(
      CommandNode<ServerCommandSource> root,
      LiteralArgumentBuilder<ServerCommandSource> argumentBuilder,
      boolean positive,
      CommandRegistryAccess commandRegistryAccess,
      CallbackInfoReturnable<ArgumentBuilder<ServerCommandSource, ?>> cir) {
    argumentBuilder.then(CommandManager.literal("cmd:expression")
        .then(addConditionLogic(
            root,
            CommandManager.argument("expression", StringArgumentType.string()),
            positive,
            context -> {
              var r = Expression.parse(StringArgumentType.getString(context, "expression"));
              if (r.error().isPresent())
                throw Commander.EXPRESSION_EXCEPTION.create(r.error().get().message());

              LootContext context1 = new LootContext.Builder(new LootContextParameterSet.Builder(
                          context.getSource().getWorld())
                      .add(LootContextParameters.ORIGIN, context.getSource().getPosition())
                      .addOptional(
                          LootContextParameters.THIS_ENTITY, context.getSource().getEntity())
                      .build(LootContextTypes.COMMAND))
                  .build(null);

              return r.result()
                  .flatMap(expression -> Optional.ofNullable(expression.eval(context1)))
                  .map(Expression.Result::getAsBoolean)
                  .orElse(false);
            })));
  }
}
