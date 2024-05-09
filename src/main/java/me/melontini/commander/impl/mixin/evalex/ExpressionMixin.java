package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Expression.class, remap = false)
public class ExpressionMixin {

    @Shadow @Final private ExpressionConfiguration configuration;

    /**
     * @author melontini
     * @reason avoid double object construction
     */
    @Overwrite
    public EvaluationValue convertValue(Object value) {
        return configuration.getEvaluationValueConverter().convertObject(value, configuration);
    }
}
