package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Expression.class, remap = false)
public abstract class ExpressionMixin implements me.melontini.commander.api.expression.Expression {

    @Shadow public abstract String getExpressionString();

    @Override
    public Result eval(LootContext context) {
        return (Result) (Object) EvalUtils.evaluate(context, (Expression) (Object) this);
    }

    @Override
    public String original() {
        return getExpressionString();
    }
}
