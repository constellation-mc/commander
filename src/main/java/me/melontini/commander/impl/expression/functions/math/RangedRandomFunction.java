package me.melontini.commander.impl.expression.functions.math;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.functions.CustomInlinerFunction;
import me.melontini.dark_matter.api.base.util.MathUtil;

import java.math.BigDecimal;

@FunctionParameter(name = "min")
@FunctionParameter(name = "max")
public class RangedRandomFunction extends AbstractFunction implements CustomInlinerFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        BigDecimal min = par[0].getNumberValue();
        BigDecimal max = par[1].getNumberValue();
        return expression.convertValue(min.compareTo(max) >= 0 ? min : BigDecimal.valueOf(MathUtil.threadRandom().nextDouble()).multiply(max.subtract(min)).add(min));
    }

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        return null;
    }
}
