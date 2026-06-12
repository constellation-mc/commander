package dev.zenfyr.commander.impl.expression.function;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
public class LengthFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    EvaluationValue value = par[0];

    if (value.isStringValue())
      return EvaluationValue.numberValue(value.getStringValue().length());
    if (value.isArrayValue())
      return EvaluationValue.numberValue(value.getArrayValue().size());
    if (value.isStructureValue())
      return EvaluationValue.numberValue(value.getStructureValue().size());
    if (value.isDurationValue())
      return EvaluationValue.numberValue(value.getDurationValue().toMillis());
    if (value.isDateTimeValue())
      return EvaluationValue.numberValue(value.getDateTimeValue().toEpochMilli());

    return EvaluationValue.numberValue(0);
  }
}
