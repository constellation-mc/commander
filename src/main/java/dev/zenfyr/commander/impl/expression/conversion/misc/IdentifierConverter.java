package dev.zenfyr.commander.impl.expression.conversion.misc;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import net.minecraft.resources.ResourceLocation;

public class IdentifierConverter implements ConverterIfc {
  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    return EvaluationValue.stringValue(object.toString());
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof ResourceLocation;
  }
}
