package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.data.EvaluationValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EvaluationValue.class, remap = false)
public interface EvaluationValueAccessor {

    @Invoker("<init>")
    static EvaluationValue commander$init(Object value, EvaluationValue.DataType dataType) {
        throw new IllegalStateException();
    }
}
