package me.melontini.commander.impl.expression.functions.arrays;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.ArrayValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import com.google.common.collect.Lists;
import me.melontini.dark_matter.api.base.util.Exceptions;

import java.util.List;

import static me.melontini.commander.impl.expression.EvalUtils.runLambda;

@FunctionParameter(name = "array")
@FunctionParameter(name = "function", isLazy = true)
public class ArrayMap extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(EvaluationContext expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        List<EvaluationValue> array = par[0].getArrayValue();
        ASTNode function = par[1].getExpressionNode();

        return ArrayValue.of(Lists.transform(array, input -> Exceptions.supply(() -> runLambda(expression, input, function))));
    }

    @Override
    public EvaluationValue inlineFunction(Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
        return null;
    }
}
