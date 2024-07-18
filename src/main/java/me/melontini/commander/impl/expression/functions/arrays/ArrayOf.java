package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.extensions.convert.LazyArrayConverter;

import java.util.Arrays;

@FunctionParameter(name = "args", isVarArg = true)
public class ArrayOf extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        if (par.length == 0) return LazyArrayConverter.EMPTY;
        return EvaluationValue.arrayValue(Arrays.asList(par));
    }
}
