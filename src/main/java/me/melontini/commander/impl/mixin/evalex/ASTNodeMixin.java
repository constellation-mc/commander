package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ASTNode;
import me.melontini.commander.impl.util.ASTInliner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ASTNode.class)
public class ASTNodeMixin implements ASTInliner.InlinedNode {

    @Unique
    private EvaluationValue cmd$value;

    @Override
    public EvaluationValue cmd$value() {
        return this.cmd$value;
    }

    @Override
    public void cmd$value(EvaluationValue value) {
        this.cmd$value = value;
    }
}
