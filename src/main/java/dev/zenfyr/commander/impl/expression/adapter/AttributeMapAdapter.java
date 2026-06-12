package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AttributeMapAdapter implements TypeAdapter<AttributeMap> {

  @Override
  public EvaluationValue getProperty(
      PropertyAdapterContext<AttributeMap> ctx, EvaluationContext evalContext) {
    if (!ResourceLocation.isValidResourceLocation(ctx.getName())) return EvaluationValue.UNDEFINED;
    ResourceLocation location = ResourceLocation.tryParse(ctx.getName());
    Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(location);
    if (attribute == null) return EvaluationValue.UNDEFINED;
    return Expression.convertValue(ctx.getObject().getValue(attribute), ctx.getConfig());
  }
}
