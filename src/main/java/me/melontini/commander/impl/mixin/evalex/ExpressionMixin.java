package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Mixin(value = Expression.class, remap = false)
public abstract class ExpressionMixin implements me.melontini.commander.api.expression.Expression {

    @Unique private static final TreeMap<?, ?> PAIN = new TreeMap<>();

    @Shadow public abstract String getExpressionString();

    @Shadow public abstract Expression with(String variable, Object value);

    @Shadow public abstract DataAccessorIfc getDataAccessor();

    @Mutable
    @Shadow @Final private Map<String, EvaluationValue> constants;

    @Redirect(at = @At(value = "NEW", target = "(Ljava/util/Comparator;)Ljava/util/TreeMap;"), method = "<init>(Ljava/lang/String;Lcom/ezylang/evalex/config/ExpressionConfiguration;)V")
    private TreeMap<?, ?> skipMap(Comparator<?> comparator) {
        return PAIN;//New mixin versions prohibit returning null. For some reason.
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;putAll(Ljava/util/Map;)V"), method = "<init>(Ljava/lang/String;Lcom/ezylang/evalex/config/ExpressionConfiguration;)V")
    private void putOurConstants(Map<?, ?> instance, Map<?, ?> map) {
        this.constants = EvalUtils.CONSTANTS;
    }

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
