package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Expression.class, remap = false)
public abstract class ExpressionMixin implements me.melontini.commander.api.expression.Expression {

    @Shadow @Final private ExpressionConfiguration configuration;

    @Shadow public abstract String getExpressionString();

    /**
     * @author melontini
     * @reason avoid double object construction
     */
    @Overwrite
    public EvaluationValue convertValue(Object value) {
        return configuration.getEvaluationValueConverter().convertObject(value, configuration);
    }

    @Override
    public Result eval(LootContext context) {
        return (Result) (Object) EvalUtils.evaluate(context, (Expression) (Object) this);
    }

    @Override
    public String original() {
        return getExpressionString();
    }
}
