package me.melontini.commander.impl.util.loot;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.impl.Commander;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public record ExpressionLootCondition(Expression expression) implements LootCondition {
    public static final Codec<ExpressionLootCondition> CODEC = Expression.CODEC.xmap(ExpressionLootCondition::new, ExpressionLootCondition::expression);

    @Override
    public LootConditionType getType() {
        return Commander.EXPRESSION_CONDITION;
    }

    @Override
    public boolean test(LootContext context) {
        return expression().eval(context).getAsBoolean();
    }
}
