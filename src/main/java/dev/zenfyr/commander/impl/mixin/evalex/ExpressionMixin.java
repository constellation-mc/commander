package dev.zenfyr.commander.impl.mixin.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Expression.class, remap = false)
public abstract class ExpressionMixin implements dev.zenfyr.commander.api.expression.Expression {

  @Shadow
  public abstract String getExpressionString();

  @Override
  public @NotNull Result eval(LootContext context, Map<String, Result> parameters) {
    return (Result) (Object) EvalUtils.evaluate(
        context, (Expression) (Object) this, (Map<String, EvaluationValue>) (Map<?, ?>) parameters);
  }

  @Override
  public String original() {
    return getExpressionString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(original());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Expression exp)) return false;
    return Objects.equals(original(), exp.getExpressionString());
  }
}
