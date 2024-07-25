package me.melontini.commander.impl.expression.functions.math;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import java.math.BigDecimal;
import java.util.List;
import me.melontini.dark_matter.api.base.util.MathUtil;
import org.jetbrains.annotations.Nullable;

@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class RangedRandomFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    BigDecimal min = par[0].getNumberValue();
    BigDecimal max = par[1].getNumberValue();
    return context
        .expression()
        .convertValue(
            min.compareTo(max) >= 0
                ? min
                : BigDecimal.valueOf(MathUtil.threadRandom().nextDouble())
                    .multiply(max.subtract(min))
                    .add(min));
  }

  @Override
  public @Nullable EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return null;
  }
}
