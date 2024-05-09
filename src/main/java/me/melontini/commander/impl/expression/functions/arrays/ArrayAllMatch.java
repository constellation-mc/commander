package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;

import java.util.List;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

@FunctionParameter(name = "array")
@FunctionParameter(name = "predicate", isLazy = true)
public class ArrayAllMatch extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        List<EvaluationValue> array = par[0].getArrayValue();
        ASTNode predicate = par[1].getExpressionNode();

        return EvaluationValue.booleanValue(array.stream().anyMatch(value -> runLambda(expression, value, predicate).getBooleanValue()));
    }
}
