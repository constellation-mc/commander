package dev.zenfyr.commander.impl.expression.function.math;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.number.NumberValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import dev.zenfyr.pulsar.util.MathUtil;

@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class RangedRandomFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    NumberValue min = par[0].getNumberValue();
    NumberValue max = par[1].getNumberValue();

    return EvaluationValue.numberValue(MathUtil.nextDouble(min.doubleValue(), max.doubleValue()));
  }
}
