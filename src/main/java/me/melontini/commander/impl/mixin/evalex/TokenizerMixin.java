package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.parser.Tokenizer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Tokenizer.class, remap = false)
public class TokenizerMixin {

    @Shadow private int currentChar;

    @ModifyExpressionValue(method = "isAtIdentifierChar", at = @At(value = "INVOKE", target = "Ljava/lang/Character;isDigit(I)Z"))
    private boolean commander$allowColons(boolean original) {
        return original || currentChar == ':';
    }
}
