package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackAdapter extends ReflectiveTypeAdapter<ItemStack> {

  private static final CompoundTag EMPTY = new CompoundTag();

  public EvaluationValue prop_key(PropertyAdapterContext<ItemStack> ctx) {
    return Expression.convertValue(
        EvalUtils.fromHolder(ctx.getObject().getItemHolder(), BuiltInRegistries.ITEM),
        ctx.getConfig());
  }

  public EvaluationValue prop_nbt(PropertyAdapterContext<ItemStack> ctx) {
    return Expression.convertValue(
        ctx.getObject().getTag() == null ? EMPTY : ctx.getObject().getTag(), ctx.getConfig());
  }

  public EvaluationValue prop_count(PropertyAdapterContext<ItemStack> ctx) {
    return Expression.convertValue(ctx.getObject().getCount(), ctx.getConfig());
  }
}
