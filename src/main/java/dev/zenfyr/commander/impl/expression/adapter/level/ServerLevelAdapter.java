package dev.zenfyr.commander.impl.expression.adapter.level;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import dev.zenfyr.commander.impl.expression.adapter.AdapterUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;

public class ServerLevelAdapter extends LevelAdapter<ServerLevel> {

  public EvaluationValue prop_server(PropertyAdapterContext<ServerLevel> ctx) {
    return Expression.convertValue(ctx.getObject().getServer(), ctx.getConfig());
  }

  public EvaluationValue prop_dimension(PropertyAdapterContext<ServerLevel> ctx) {
    var server = ctx.getObject().getServer();
    var registry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
    return Expression.convertValue(
        EvalUtils.fromHolder(ctx.getObject().dimensionTypeRegistration(), registry),
        ctx.getConfig());
  }

  public EvaluationValue method_biome(
      MethodAdapterContext<ServerLevel> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    TypeAdapter.validateArgCount(ctx, 3);
    var server = ctx.getObject().getServer();
    var registry = server.registryAccess().registryOrThrow(Registries.BIOME);
    return Expression.convertValue(
        EvalUtils.fromHolder(
            ctx.getObject().getBiome(AdapterUtils.blockPos(evalContext, ctx.getArgs())), registry),
        evalContext);
  }
}
