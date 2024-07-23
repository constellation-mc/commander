package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.ArrayValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

import java.util.Arrays;

@FunctionParameter(name = "args", isVarArg = true)
public class ArrayOf extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(EvaluationContext context, Token functionToken, EvaluationValue... par) throws EvaluationException {
        return ArrayValue.of(Arrays.asList(par));
    }
}
