package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;

import java.math.BigDecimal;

@FunctionParameter(name = "value")
public class LengthFunction extends AbstractFunction implements CustomInlinerFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        EvaluationValue value = par[0];

        if (value.isStringValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getStringValue().length()));
        if (value.isArrayValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getArrayValue().size()));
        if (value.isStructureValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getStructureValue().size()));
        if (value.isDurationValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getDurationValue().toMillis()));

        return EvaluationValue.numberValue(BigDecimal.ZERO);
    }

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) {
        var value = CustomInlinerFunction.getNodeValue(node.getParameters().get(0));
        if (value == null) return null;

        if (value.isStringValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getStringValue().length()));
        if (value.isArrayValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getArrayValue().size()));
        if (value.isDurationValue()) return EvaluationValue.numberValue(BigDecimal.valueOf(value.getDurationValue().toMillis()));

        return null;
    }
}
