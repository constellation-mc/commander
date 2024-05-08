package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Expression.class, remap = false)
public interface ExpressionAccessor {

    @Mutable
    @Accessor("constants")
    void commander$constants(Map<String, EvaluationValue> map);
}
