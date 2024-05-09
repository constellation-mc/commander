package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.config.MapBasedFunctionDictionary;
import com.ezylang.evalex.functions.FunctionIfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = MapBasedFunctionDictionary.class, remap = false)
public interface MapBasedFunctionDictionaryAccessor {

    @Accessor("functions")
    Map<String, FunctionIfc> commander$getFunctions();
}
