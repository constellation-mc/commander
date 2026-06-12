package dev.zenfyr.commander.impl.expression.function.math;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.number.NumberValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class ClampFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    NumberValue value = par[0].getNumberValue();
    NumberValue min = par[1].getNumberValue();
    NumberValue max = par[2].getNumberValue();
    return EvaluationValue.numberValue(value.compareTo(min) < 0 ? min : value.min(max));
  }
}
