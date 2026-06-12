package dev.zenfyr.commander.impl.expression.adapter.entity;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.AttachmentTargetAdapter;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;

public class EntityAdapter<T extends Entity> extends ReflectiveTypeAdapter<T>
    implements AttachmentTargetAdapter {

  public EvaluationValue prop_key(PropertyAdapterContext<T> ctx) {
    return Expression.convertValue(
        EvalUtils.fromHolder(
            ctx.getObject().getType().builtInRegistryHolder(), BuiltInRegistries.ENTITY_TYPE),
        ctx.getConfig());
  }

  public EvaluationValue prop_x(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getX());
  }

  public EvaluationValue prop_y(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getY());
  }

  public EvaluationValue prop_z(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getZ());
  }

  public EvaluationValue prop_blockX(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getBlockX());
  }

  public EvaluationValue prop_blockY(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getBlockY());
  }

  public EvaluationValue prop_blockZ(PropertyAdapterContext<T> ctx) {
    return EvaluationValue.numberValue(ctx.getObject().getBlockZ());
  }

  public EvaluationValue prop_nbt(PropertyAdapterContext<T> ctx) {
    return Expression.convertValue(
        NbtPredicate.getEntityTagToCompare(ctx.getObject()), ctx.getConfig());
  }

  public EvaluationValue method_isAlive(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isAlive());
  }

  public EvaluationValue method_isOnFire(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isOnFire());
  }

  public EvaluationValue method_isInWater(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isInWater());
  }

  public EvaluationValue method_isInLava(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isInLava());
  }

  public EvaluationValue method_isFreezing(MethodAdapterContext<T> ctx) {
    return EvaluationValue.booleanValue(ctx.getObject().isFreezing());
  }
}
