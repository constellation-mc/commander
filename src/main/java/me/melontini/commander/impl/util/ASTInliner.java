package me.melontini.commander.impl.util;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.functions.CustomInlinerFunction;

public class ASTInliner {

    public static void optimize(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        if (getValue(node) != null) return;

        for (ASTNode parameter : node.getParameters()) {
            optimize(expression, parameter);
        }

        Token token = node.getToken();
        switch (token.getType()) {
            case NUMBER_LITERAL, STRING_LITERAL -> setValue(node, expression.evaluateSubtree(node));
            case POSTFIX_OPERATOR, PREFIX_OPERATOR -> {
                var value = getValue(node.getParameters().get(0));
                if (value != null) setValue(node, token.getOperatorDefinition().evaluate(expression, token, value));
            }
            case INFIX_OPERATOR -> {
                var left = getValue(node.getParameters().get(0));
                var right = getValue(node.getParameters().get(1));
                if (right != null && left != null) setValue(node, token.getOperatorDefinition().evaluate(expression, token, left, right));
            }
            case FUNCTION -> {
                FunctionIfc function = token.getFunctionDefinition();

                if (function instanceof CustomInlinerFunction inlinerFunction) {
                    var value = inlinerFunction.cmd$inlineFunction(expression, node);
                    if (value != null) setValue(node, value);
                    break;
                }

                if (node.getParameters().stream().allMatch(node1 -> getValue(node1) != null)) {
                    EvaluationValue[] parameters = node.getParameters().stream().map(ASTInliner::getValue).toArray(EvaluationValue[]::new);
                    function.validatePreEvaluation(token, parameters);
                    setValue(node, function.evaluate(expression, token, parameters));
                }
            }
            case VARIABLE_OR_CONSTANT -> {
                EvaluationValue result = expression.getConstants().get(token.getValue());
                if (result != null) setValue(node, result);
            }
        }
    }

    public static void setValue(ASTNode node, EvaluationValue value) {
        ((InlinedNode) (Object) node).cmd$value(value);
    }
    public static EvaluationValue getValue(ASTNode node) {
        return ((InlinedNode) (Object) node).cmd$value();
    }

    public interface InlinedNode {
        EvaluationValue cmd$value();
        void cmd$value(EvaluationValue value);
    }
}
