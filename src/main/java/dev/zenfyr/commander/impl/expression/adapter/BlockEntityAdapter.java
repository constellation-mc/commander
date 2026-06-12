package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.adapter.core.AttachmentTargetAdapter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityAdapter extends ReflectiveTypeAdapter<BlockEntity>
    implements AttachmentTargetAdapter {

  public EvaluationValue prop_key(PropertyAdapterContext<BlockEntity> ctx) {
    return Expression.convertValue(
        BlockEntityType.getKey(ctx.getObject().getType()), ctx.getConfig());
  }

  public EvaluationValue prop_nbt(PropertyAdapterContext<BlockEntity> ctx) {
    return Expression.convertValue(ctx.getObject().saveWithFullMetadata(), ctx.getConfig());
  }
}
