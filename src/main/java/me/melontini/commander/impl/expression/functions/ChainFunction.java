package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

@FunctionParameter(name = "value")
@FunctionParameter(name = "actions", isLazy = true, isVarArg = true)
public class ChainFunction extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        var value = par[0];
        for (int i = 1; i < par.length; i++) {
            value = runLambda(expression, value, par[i].getExpressionNode());
        }
        return value;
    }
}
