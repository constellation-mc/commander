package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import java.math.BigDecimal;
import java.util.List;
import me.melontini.commander.impl.expression.EvalUtils;
import org.jetbrains.annotations.Nullable;

@FunctionParameter(name = "value")
public class LengthFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    EvaluationValue value = par[0];

    if (value.isStringValue())
      return context.expression().convertValue(value.getStringValue().length());
    if (value.isArrayValue())
      return context.expression().convertValue(value.getArrayValue().size());
    if (value.isStructureValue())
      return context.expression().convertValue(value.getStructureValue().size());
    if (value.isDurationValue())
      return context.expression().convertValue(value.getDurationValue().toMillis());

    return NumberValue.of(BigDecimal.ZERO);
  }

  @Override
  public @Nullable EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return EvalUtils.valueOrEmpty(parameters.get(0))
        .map(value -> {
          if (value.isStringValue())
            return expression.convertValue(value.getStringValue().length());
          if (value.isDurationValue())
            return expression.convertValue(value.getDurationValue().toMillis());
          return null;
        })
        .orElse(null);
  }
}
