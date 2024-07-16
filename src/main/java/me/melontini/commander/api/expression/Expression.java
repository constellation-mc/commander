package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.expression.EvalUtils;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public interface Expression extends Function<LootContext, Expression.Result> {

    Codec<Expression> CODEC = Codec.STRING.comapFlatMap(Expression::parse, Expression::original);

    static DataResult<Expression> parse(String expression) {
        return (DataResult<Expression>) (Object) EvalUtils.parseExpression(expression);
    }

    @Override
    default Result apply(LootContext context) {
        return this.eval(context);
    }

    Result eval(LootContext context);
    String original();

    /**
     * Sets an expression variable.<br/>
     * The expression object will hold on to the variable until it is cleared by calling this method with a {@code null} value.
     */
    @ApiStatus.Experimental
    Expression variable(String variable, Object value);

    interface Result {
        BigDecimal getAsDecimal();
        boolean getAsBoolean();
        String getAsString();
        Instant getAsInstant();
        Duration getAsDuration();

        boolean isDecimalValue();
        boolean isBooleanValue();
        boolean isStringValue();
        boolean isInstantValue();
        boolean isDurationValue();
    }
}
