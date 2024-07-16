package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "key", isVarArg = true)
public class HasContextFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        return switch (par.length) {
            case 0 -> EvaluationValue.TRUE;
            case 1 -> expression.getDataAccessor().getData(par[0].getStringValue()) != null ? EvaluationValue.TRUE : EvaluationValue.FALSE;
            default -> {
                var da = expression.getDataAccessor();
                for (EvaluationValue value : par) {
                    if (da.getData(value.getStringValue()) == null) yield  EvaluationValue.FALSE;
                }
                yield  EvaluationValue.TRUE;
            }
        };
    }
}
