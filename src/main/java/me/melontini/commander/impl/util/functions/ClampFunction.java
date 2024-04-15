package me.melontini.commander.impl.util.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import net.minecraft.util.math.MathHelper;

@FunctionParameter(name = "value")
@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class ClampFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        return expression.convertDoubleValue(MathHelper.clamp(par[0].getNumberValue().doubleValue(), par[1].getNumberValue().doubleValue(), par[2].getNumberValue().doubleValue()));
    }
}
