package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.basic.RandomFunction;
import com.ezylang.evalex.functions.datetime.DateTimeNowFunction;
import com.ezylang.evalex.functions.datetime.DateTimeTodayFunction;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import me.melontini.commander.impl.expression.functions.CustomInlinerFunction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({RandomFunction.class, DateTimeNowFunction.class,
        DateTimeTodayFunction.class})
public class NonInlineableFunctionMixin implements CustomInlinerFunction {

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        return null;
    }
}
