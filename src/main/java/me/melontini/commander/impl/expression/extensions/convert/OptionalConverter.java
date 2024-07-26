package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.types.NullValue;
import java.util.Optional;

public class OptionalConverter implements ConverterIfc {

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    Optional<?> optional = (Optional<?>) object;
    return optional.isEmpty()
        ? NullValue.of()
        : configuration.getEvaluationValueConverter().convertObject(optional.get(), configuration);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof Optional<?>;
  }
}
