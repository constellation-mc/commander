package me.melontini.commander.impl.expression.operators;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.operators.AbstractOperator;
import com.ezylang.evalex.operators.InfixOperator;
import com.ezylang.evalex.operators.OperatorIfc;
import com.ezylang.evalex.parser.Token;

// The `?` operator. Allows users to easily handle null values.
//
// This is shorter than writing an `if(a == null, a, b)` statement. `a ? b`
@InfixOperator(precedence = OperatorIfc.OPERATOR_PRECEDENCE_OR, operandsLazy = true)
public class SafeOrOperator extends AbstractOperator {

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token operatorToken, EvaluationValue... operands)
      throws EvaluationException {
    var result = context.expression().evaluateSubtree(operands[0].getExpressionNode(), context);
    return result.isNullValue()
        ? context.expression().evaluateSubtree(operands[1].getExpressionNode(), context)
        : result;
  }
}
