package me.melontini.commander.impl.util.loot;

import com.ezylang.evalex.Expression;
import com.mojang.serialization.Codec;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.util.eval.EvalUtils;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public record ExpressionLootCondition(Expression expression) implements LootCondition {
    public static final Codec<ExpressionLootCondition> CODEC = Codec.STRING
            .comapFlatMap(EvalUtils::parseExpression, exp -> exp.getExpressionString().replace("__idcl__", ":"))
            .xmap(ExpressionLootCondition::new, ExpressionLootCondition::expression);

    @Override
    public LootConditionType getType() {
        return Commander.EXPRESSION_CONDITION;
    }

    @Override
    public boolean test(LootContext context) {
        return EvalUtils.evaluate(context, expression).getBooleanValue();
    }
}
