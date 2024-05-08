package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ExpressionConfiguration.class, remap = false)
public interface ExpressionConfigurationAccessor {

    @Mutable
    @Accessor("defaultConstants")
    void commander$defaultConstants(Map<String, EvaluationValue> map);
}
