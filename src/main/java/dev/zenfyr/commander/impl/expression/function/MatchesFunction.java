package dev.zenfyr.commander.impl.expression.function;

import static dev.zenfyr.commander.impl.expression.EvalUtils.runLambda;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value")
@FunctionParameter(name = "predicate", isLazy = true)
@FunctionParameter(name = "ifTrue", isLazy = true)
@FunctionParameter(name = "ifFalse", isLazy = true)
public class MatchesFunction extends AbstractFunction {

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    EvaluationValue value = par[0];
    boolean predicate = runLambda(context, value, par[1].getExpressionNode()).getBooleanValue();

    return runLambda(context, value, par[predicate ? 2 : 3].getExpressionNode());
  }
}
