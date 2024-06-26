package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.EvalUtils;

@FunctionParameter(name = "struct")
@FunctionParameter(name = "key", isVarArg = true)
public class StructContainsKeyFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        return switch (par.length) {
            case 1 -> EvalUtils.TRUE;
            case 2 -> par[0].getStructureValue().containsKey(par[1].getStringValue()) ? EvalUtils.TRUE : EvalUtils.FALSE;
            default -> {
                var struct = par[0].getStructureValue();
                for (int i = 1; i < par.length; i++) {
                    var key = par[i].getStringValue();
                    if (!struct.containsKey(key)) yield  EvalUtils.FALSE;
                }
                yield  EvalUtils.TRUE;
            }
        };
    }
}
