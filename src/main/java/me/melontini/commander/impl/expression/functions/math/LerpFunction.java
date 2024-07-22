package me.melontini.commander.impl.expression.functions.math;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.math.BigDecimal;

@FunctionParameter(name = "delta")
@FunctionParameter(name = "start")
@FunctionParameter(name = "end")
public class LerpFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(EvaluationContext context, Token functionToken, EvaluationValue... par) throws EvaluationException {
        BigDecimal start = par[0].getNumberValue();
        BigDecimal delta = par[1].getNumberValue();
        BigDecimal end = par[2].getNumberValue();
        return EvaluationValue.numberValue(start.add(delta.multiply(end.subtract(start))));
    }
}
