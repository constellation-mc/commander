package dev.zenfyr.commander.impl.expression.adapter.level;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import net.minecraft.server.MinecraftServer;

public class MinecraftServerAdapter extends ReflectiveTypeAdapter<MinecraftServer> {

  public EvaluationValue prop_players(PropertyAdapterContext<MinecraftServer> ctx) {
    return Expression.convertValue(ctx.getObject().getPlayerList().getPlayers(), ctx.getConfig());
  }
}
