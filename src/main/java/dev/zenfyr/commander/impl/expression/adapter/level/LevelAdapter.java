package dev.zenfyr.commander.impl.expression.adapter.level;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.AttachmentTargetAdapter;
import net.minecraft.world.level.Level;

public class LevelAdapter<T extends Level> extends ReflectiveTypeAdapter<T>
    implements AttachmentTargetAdapter, BlockGetterAdapter {

  public EvaluationValue prop_time(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getDayTime());
  }

  public EvaluationValue prop_gameTime(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getGameTime());
  }

  public EvaluationValue prop_players(PropertyAdapterContext<T> ctx) {
    return Expression.convertValue(ctx.getObject().players(), ctx.getConfig());
  }

  public EvaluationValue method_isDay(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isDay());
  }
}
