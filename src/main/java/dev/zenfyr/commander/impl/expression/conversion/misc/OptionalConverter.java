package dev.zenfyr.commander.impl.expression.conversion.misc;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import java.util.Optional;

public class OptionalConverter implements ConverterIfc {

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    Optional<?> optional = (Optional<?>) object;
    return optional.isEmpty()
        ? EvaluationValue.NULL_VALUE
        : configuration.getEvaluationValueConverter().convertObject(optional.get(), configuration);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof Optional<?>;
  }
}
