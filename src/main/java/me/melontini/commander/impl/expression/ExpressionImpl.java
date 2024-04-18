package me.melontini.commander.impl.expression;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import net.minecraft.loot.context.LootContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public record ExpressionImpl(Expression expression) implements me.melontini.commander.api.expression.Expression {

    @Override
    public Result eval(LootContext context) {
        return new ResultImpl(EvalUtils.evaluate(context, expression()));
    }

    @Override
    public String original() {
        return expression().getExpressionString().replace("__idcl__", ":");
    }

    public record ResultImpl(EvaluationValue value) implements me.melontini.commander.api.expression.Expression.Result {

        @Override
        public BigDecimal getAsDecimal() {
            return value().getNumberValue();
        }

        @Override
        public boolean getAsBoolean() {
            return value().getBooleanValue();
        }

        @Override
        public String getAsString() {
            return value().getStringValue();
        }

        @Override
        public Instant getAsInstant() {
            return value().getDateTimeValue();
        }

        @Override
        public Duration getAsDuration() {
            return value().getDurationValue();
        }
    }
}
