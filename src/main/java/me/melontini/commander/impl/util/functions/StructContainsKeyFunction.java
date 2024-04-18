package me.melontini.commander.impl.util.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "struct")
@FunctionParameter(name = "key")
public class StructContainsKeyFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        EvaluationValue value = par[0];
        if (value.isStructureValue()) {
            String key = par[1].getStringValue();
            return expression.convertValue(value.getStructureValue().containsKey(key));
        }
        throw EvaluationException.ofUnsupportedDataTypeInOperation(functionToken);
    }
}
