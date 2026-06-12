package dev.zenfyr.commander.impl.expression.adapter.entity;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityAdapter<T extends LivingEntity> extends EntityAdapter<T> {

  public EvaluationValue prop_attributes(PropertyAdapterContext<T> ctx) {
    return Expression.convertValue(ctx.getObject().getAttributes(), ctx.getConfig());
  }

  public EvaluationValue prop_health(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getHealth());
  }
}
