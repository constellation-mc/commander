package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.functions.CustomInlinerFunction;

import java.util.List;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

@FunctionParameter(name = "array")
@FunctionParameter(name = "predicate", isLazy = true)
public class ArrayAllMatch extends AbstractFunction implements CustomInlinerFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        List<EvaluationValue> array = par[0].getArrayValue();
        ASTNode predicate = par[1].getExpressionNode();

        return array.stream().allMatch(value -> runLambda(expression, value, predicate).getBooleanValue()) ? EvaluationValue.TRUE : EvaluationValue.FALSE;
    }

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        var value = CustomInlinerFunction.getNodeValue(node.getParameters().get(0));
        if (value == null) return null;
        return evaluate(expression, node.getToken(), value, EvaluationValue.expressionNodeValue(node.getParameters().get(1)));
    }
}
