package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.util.ASTInliner;

public interface CustomInlinerFunction {

    EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException;

    static EvaluationValue getNodeValue(ASTNode node) {
        return ASTInliner.getValue(node);
    }

    static EvaluationValue withConstant(Expression expression, ASTNode node, String variable, EvaluationValue value) throws ParseException, EvaluationException {
        if (node.getParameters().isEmpty()) return null;
        for (ASTNode parameter : node.getParameters()) {
            if (parameter.getToken().getType() != Token.TokenType.VARIABLE_OR_CONSTANT || !variable.equals(parameter.getToken().getValue())) continue;
            ASTInliner.setValue(parameter, value);
        }
        ASTInliner.optimize(expression, node);
        return ASTInliner.getValue(node);
    }
}
