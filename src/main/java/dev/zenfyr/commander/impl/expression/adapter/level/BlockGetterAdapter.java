package dev.zenfyr.commander.impl.expression.adapter.level;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.AdapterUtils;
import net.minecraft.world.level.BlockGetter;

public interface BlockGetterAdapter {

  default EvaluationValue method_block(
      MethodAdapterContext<BlockGetter> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    TypeAdapter.validateArgCount(ctx, 3);
    return Expression.convertValue(
        ctx.getObject().getBlockState(AdapterUtils.blockPos(evalContext, ctx.getArgs())),
        evalContext);
  }

  default EvaluationValue method_blockEntity(
      MethodAdapterContext<BlockGetter> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    TypeAdapter.validateArgCount(ctx, 3);
    return Expression.convertValue(
        ctx.getObject().getBlockEntity(AdapterUtils.blockPos(evalContext, ctx.getArgs())),
        evalContext);
  }

  default EvaluationValue method_fluid(
      MethodAdapterContext<BlockGetter> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    TypeAdapter.validateArgCount(ctx, 3);
    return Expression.convertValue(
        ctx.getObject().getFluidState(AdapterUtils.blockPos(evalContext, ctx.getArgs())),
        evalContext);
  }
}
