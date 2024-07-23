package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NullValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "array")
public class ArrayFindFirst extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(EvaluationContext context, Token functionToken, EvaluationValue... par) throws EvaluationException {
        return par[0].getArrayValue().stream().findFirst().orElse(NullValue.of());
    }
}
