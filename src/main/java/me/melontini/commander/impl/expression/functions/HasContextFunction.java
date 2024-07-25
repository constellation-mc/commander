package me.melontini.commander.impl.expression.functions;

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
import org.jetbrains.annotations.Nullable;

@FunctionParameter(name = "key", isVarArg = true)
public class HasContextFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(EvaluationContext context, Token token, EvaluationValue... par)
      throws EvaluationException {
    return switch (par.length) {
      case 0 -> BooleanValue.TRUE;
      case 1 -> BooleanValue.of(
          context.expression().getDataAccessor().getData(par[0].getStringValue(), token, context)
              != null);
      default -> {
        var da = context.expression().getDataAccessor();
        for (EvaluationValue value : par) {
          if (da.getData(value.getStringValue(), token, context) == null) yield BooleanValue.FALSE;
        }
        yield BooleanValue.TRUE;
      }
    };
  }

  @Override
  public @Nullable EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return null;
  }
}
