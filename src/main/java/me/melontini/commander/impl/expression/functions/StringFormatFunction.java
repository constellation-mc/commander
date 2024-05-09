package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.util.Arrays;

@FunctionParameter(name = "pattern")
@FunctionParameter(name = "args", isVarArg = true)
public class StringFormatFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        if (par.length == 1) return EvaluationValue.stringValue(String.format(par[0].getStringValue()));
        return EvaluationValue.stringValue(String.format(par[0].getStringValue(), Arrays.stream(Arrays.copyOfRange(par, 1, par.length)).map(EvaluationValue::getValue).toArray()));
    }
}
