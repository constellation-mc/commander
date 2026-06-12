package dev.zenfyr.commander.impl.expression.adapter.core;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import net.minecraft.core.Vec3i;

public class Vec3iAdapter<T extends Vec3i> extends ReflectiveTypeAdapter<T> {

  public EvaluationValue prop_x(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getX());
  }

  public EvaluationValue prop_y(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getY());
  }

  public EvaluationValue prop_z(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getZ());
  }
}
