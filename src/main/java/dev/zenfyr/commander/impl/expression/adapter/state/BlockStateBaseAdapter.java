package dev.zenfyr.commander.impl.expression.adapter.state;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockStateBaseAdapter extends StateHolderAdapter<BlockBehaviour.BlockStateBase> {

  public EvaluationValue prop_key(PropertyAdapterContext<BlockBehaviour.BlockStateBase> ctx) {
    return Expression.convertValue(
        EvalUtils.fromHolder(ctx.getObject().getBlockHolder(), BuiltInRegistries.BLOCK),
        ctx.getConfig());
  }
}
