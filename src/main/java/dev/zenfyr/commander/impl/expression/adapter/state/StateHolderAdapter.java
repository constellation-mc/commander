package dev.zenfyr.commander.impl.expression.adapter.state;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import java.util.Map;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class StateHolderAdapter<A extends StateHolder<?, ?>> extends ReflectiveTypeAdapter<A> {

  public EvaluationValue method_prop(
      MethodAdapterContext<StateHolder<?, ?>> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    TypeAdapter.validateArgCount(ctx, 1);
    for (Map.Entry<Property<?>, Comparable<?>> entry :
        ctx.getObject().getValues().entrySet()) {
      if (entry.getKey().getName().equals(ctx.getArgs()[0].solve(evalContext).getStringValue()))
        return Expression.convertValue(entry.getValue(), evalContext);
    }
    return EvaluationValue.UNDEFINED;
  }
}
