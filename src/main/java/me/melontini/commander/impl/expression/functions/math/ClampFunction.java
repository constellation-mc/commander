package me.melontini.commander.impl.expression.functions.math;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.math.BigDecimal;

@FunctionParameter(name = "value")
@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class ClampFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(EvaluationContext context, Token functionToken, EvaluationValue... par) throws EvaluationException {
        BigDecimal value = par[0].getNumberValue();
        BigDecimal min = par[1].getNumberValue();
        BigDecimal max = par[2].getNumberValue();
        return EvaluationValue.numberValue(value.compareTo(min) < 0 ? min : value.min(max));
    }
}
