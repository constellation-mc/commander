package dev.zenfyr.commander.impl.expression.function;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "struct")
@FunctionParameter(name = "key", isVarArg = true)
public class StructContainsKeyFunction extends AbstractFunction {

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    return switch (par.length) {
      case 1 -> EvaluationValue.TRUE;
      case 2 ->
        EvaluationValue.booleanValue(
            par[0].getStructureValue().containsKey(par[1].getStringValue()));
      default -> {
        var struct = par[0].getStructureValue();
        for (int i = 1; i < par.length; i++) {
          var key = par[i].getStringValue();
          if (!struct.containsKey(key)) yield EvaluationValue.FALSE;
        }
        yield EvaluationValue.TRUE;
      }
    };
  }
}
