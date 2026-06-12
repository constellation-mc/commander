package dev.zenfyr.commander.impl.expression.adapter.core;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import net.minecraft.core.Position;

public class PositionAdapter<T extends Position> extends ReflectiveTypeAdapter<T> {

  public EvaluationValue prop_x(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().x());
  }

  public EvaluationValue prop_y(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().y());
  }

  public EvaluationValue prop_z(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().z());
  }
}
