package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.DataAccessorIfc;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Expression.class, remap = false)
public abstract class ExpressionMixin implements me.melontini.commander.api.expression.Expression {

    @Shadow public abstract String getExpressionString();

    @Shadow public abstract Expression with(String variable, Object value);

    @Shadow public abstract DataAccessorIfc getDataAccessor();

    @Override
    public Result eval(LootContext context) {
        return (Result) (Object) EvalUtils.evaluate(context, (Expression) (Object) this);
    }

    @Override
    public me.melontini.commander.api.expression.Expression variable(String variable, Object value) {
        if (value == null) {
            getDataAccessor().setData(variable, null);
            return this;
        }
        return (me.melontini.commander.api.expression.Expression) this.with(variable, value);
    }

    @Override
    public String original() {
        return getExpressionString();
    }
}
