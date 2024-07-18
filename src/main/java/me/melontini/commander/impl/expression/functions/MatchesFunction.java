package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

@FunctionParameter(name = "value")
@FunctionParameter(name = "predicate", isLazy = true)
@FunctionParameter(name = "ifTrue", isLazy = true)
@FunctionParameter(name = "ifFalse", isLazy = true)
public class MatchesFunction extends AbstractFunction implements CustomInlinerFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        EvaluationValue value = par[0];
        boolean predicate = runLambda(expression, value, par[1].getExpressionNode()).getBooleanValue();

        return runLambda(expression, value, par[predicate ? 2 : 3].getExpressionNode());
    }

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        var value = CustomInlinerFunction.getNodeValue(node.getParameters().get(0));
        if (value == null) return null;
        EvaluationValue predicate = CustomInlinerFunction.withConstant(expression, node.getParameters().get(1), "it", value);
        if (predicate == null) return null;
        // No point in checking the other lambda if the value is constant.
        return CustomInlinerFunction.withConstant(expression, node.getParameters().get(predicate.getBooleanValue() ? 2 : 3), "it", value);
    }
}
