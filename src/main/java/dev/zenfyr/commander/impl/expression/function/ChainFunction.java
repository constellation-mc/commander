package dev.zenfyr.commander.impl.expression.function;

import static dev.zenfyr.commander.impl.expression.EvalUtils.runLambda;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
@FunctionParameter(name = "actions", isLazy = true, isVarArg = true)
public class ChainFunction extends AbstractFunction {

  @Override
  public EvaluationValue evaluate(EvaluationContext context, Token token, EvaluationValue... par)
      throws EvaluationException {
    var value = par[0];
    for (int i = 1; i < par.length; i++) {
      value = runLambda(context, value, par[i].getExpressionNode());
    }
    return value;
  }
}
