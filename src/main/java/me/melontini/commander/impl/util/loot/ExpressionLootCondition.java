package me.melontini.commander.impl.util.loot;

import com.mojang.serialization.MapCodec;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.impl.Commander;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public record ExpressionLootCondition(Expression expression) implements LootCondition {
  public static final MapCodec<ExpressionLootCondition> CODEC = Expression.CODEC
      .xmap(ExpressionLootCondition::new, ExpressionLootCondition::expression)
      .fieldOf("value");

  @Override
  public LootConditionType getType() {
    return Commander.EXPRESSION_CONDITION;
  }

  @Override
  public boolean test(LootContext context) {
    return expression().eval(context).getAsBoolean();
  }
}
