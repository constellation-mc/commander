package me.melontini.commander.impl.expression.operators;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NullValue;
import com.ezylang.evalex.operators.AbstractOperator;
import com.ezylang.evalex.operators.InfixOperator;
import com.ezylang.evalex.parser.Token;

@InfixOperator(precedence = Integer.MAX_VALUE, operandsLazy = true)
public class SafeStructureOperator extends AbstractOperator {

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token operatorToken, EvaluationValue... operands)
      throws EvaluationException {
    EvaluationValue structure =
        context.expression().evaluateSubtree(operands[0].getExpressionNode(), context);
    if (structure.isNullValue()) return structure;

    Token nameToken = operands[1].getExpressionNode().getToken();
    String name = nameToken.getValue();

    if (structure.isDataAccessorValue()) {
      var result = structure.getDataAccessorValue().getData(name, nameToken, context);
      return result == null ? NullValue.of() : result;
    }

    if (structure.isStructureValue()) {
      if (!structure.getStructureValue().containsKey(name)) {
        return NullValue.of();
      }
      return structure.getStructureValue().get(name);
    }
    throw EvaluationException.ofUnsupportedDataTypeInOperation(operatorToken);
  }
}
