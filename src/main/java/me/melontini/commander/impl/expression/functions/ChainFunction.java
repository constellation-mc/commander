package me.melontini.commander.impl.expression.functions;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import java.util.List;
import org.jetbrains.annotations.Nullable;

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

  @Override
  public boolean forceInline() {
    return true;
  }

  @Override
  public @Nullable EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return null;
  }
}
