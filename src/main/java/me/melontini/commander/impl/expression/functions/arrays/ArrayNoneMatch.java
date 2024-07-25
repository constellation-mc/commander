package me.melontini.commander.impl.expression.functions.arrays;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.BooleanValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import java.util.List;
import me.melontini.dark_matter.api.base.util.Exceptions;

@FunctionParameter(name = "array")
@FunctionParameter(name = "predicate", isLazy = true)
public class ArrayNoneMatch extends AbstractFunction {

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    List<EvaluationValue> array = par[0].getArrayValue();
    ASTNode predicate = par[1].getExpressionNode();

    return BooleanValue.of(array.stream()
        .noneMatch(value ->
            Exceptions.supply(() -> runLambda(context, value, predicate)).getBooleanValue()));
  }

  @Override
  public EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return null;
  }
}
