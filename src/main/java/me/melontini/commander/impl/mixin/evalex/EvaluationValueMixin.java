package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.EvalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = EvaluationValue.class, remap = false)
public class EvaluationValueMixin {

    /**
     * @author melontini
     * @reason fold boolean constants
     */
    @Overwrite
    public static EvaluationValue booleanValue(Boolean value) {
        return value ? EvalUtils.TRUE : EvalUtils.FALSE;
    }

    /**
     * @author melontini
     * @reason fold null constant
     */
    @Overwrite
    public static EvaluationValue nullValue() {
        return EvalUtils.NULL;
    }
}
