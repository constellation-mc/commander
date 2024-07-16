package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
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

    @ApiStatus.Experimental
    default Result evalWithVariables(LootContext context, Map<String, Object> map) {
        try {
            map.forEach(this::variable);
            return this.eval(context);
        } finally {
            map.keySet().forEach(var -> this.variable(var, null));
        }
    }

    @ApiStatus.Experimental
    default Result evalWithVariable(LootContext context, String variable, Object value) {
        try {
            return this.variable(variable, value).eval(context);
        } finally {
            this.variable(variable, null);
        }
    }

    interface Result {

        static Result convert(Object o) {
            return (Result) (Object) ReflectiveValueConverter.convert(o);
        }

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

        Object getValue();
    }
}
