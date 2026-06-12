package dev.zenfyr.commander.impl.expression.extension;

import com.mojang.serialization.MapCodec;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.impl.Commander;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public record ExpressionLootCondition(Expression expression) implements LootItemCondition {
  public static final MapCodec<ExpressionLootCondition> CODEC = Expression.CODEC
      .xmap(ExpressionLootCondition::new, ExpressionLootCondition::expression)
      .fieldOf("value");

  @Override
  public LootItemConditionType getType() {
    return Commander.EXPRESSION_CONDITION;
  }

  @Override
  public boolean test(LootContext context) {
    return expression().eval(context).getAsBoolean();
  }
}
