package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;

public class DamageSourceAdapter extends ReflectiveTypeAdapter<DamageSource> {

  public EvaluationValue prop_key(
      PropertyAdapterContext<DamageSource> ctx, EvaluationContext evalContext) {
    var server = ((LootContext) evalContext.getContext()).getLevel().getServer();
    var registry = server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
    return Expression.convertValue(
        EvalUtils.fromHolder(ctx.getObject().typeHolder(), registry), ctx.getConfig());
  }
}
