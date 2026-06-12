package dev.zenfyr.commander.impl.expression.function;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "key", isVarArg = true)
public class HasContextFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(EvaluationContext context, Token token, EvaluationValue... par)
      throws EvaluationException {
    return switch (par.length) {
      case 0 -> EvaluationValue.TRUE;
      case 1 ->
        EvaluationValue.booleanValue(context
                .getConfiguration()
                .getDataAccessor()
                .getData(par[0].getStringValue(), token, context)
            != null);
      default -> {
        var da = context.getConfiguration().getDataAccessor();
        for (EvaluationValue value : par) {
          if (da.getData(value.getStringValue(), token, context) == null)
            yield EvaluationValue.FALSE;
        }
        yield EvaluationValue.TRUE;
      }
    };
  }
}
