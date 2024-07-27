package me.melontini.commander.impl.expression.extensions.convert.misc;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.types.StringValue;
import net.minecraft.util.Identifier;

public class IdentifierConverter implements ConverterIfc {
  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    return StringValue.of(object.toString());
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof Identifier;
  }
}
