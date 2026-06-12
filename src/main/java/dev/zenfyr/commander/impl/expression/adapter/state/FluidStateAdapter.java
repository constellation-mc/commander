package dev.zenfyr.commander.impl.expression.adapter.state;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FluidState;

public class FluidStateAdapter extends StateHolderAdapter<FluidState> {

  public EvaluationValue prop_key(PropertyAdapterContext<FluidState> ctx) {
    return Expression.convertValue(
        EvalUtils.fromHolder(ctx.getObject().holder(), BuiltInRegistries.FLUID), ctx.getConfig());
  }

  public EvaluationValue prop_amount(PropertyAdapterContext<FluidState> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getAmount());
  }
}
