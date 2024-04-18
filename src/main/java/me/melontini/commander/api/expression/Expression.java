package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.ExpressionImpl;
import net.minecraft.loot.context.LootContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public interface Expression extends Function<LootContext, Expression.Result> {

    Codec<Expression> CODEC = Codec.STRING.comapFlatMap(Expression::parse, Expression::original);

    static DataResult<Expression> parse(String expression) {
        return EvalUtils.parseExpression(expression).map(ExpressionImpl::new);
    }

    @Override
    default Result apply(LootContext context) {
        return this.eval(context);
    }

    Result eval(LootContext context);
    String original();

    interface Result {
        BigDecimal getAsDecimal();
        boolean getAsBoolean();
        String getAsString();
        Instant getAsInstant();
        Duration getAsDuration();
    }
}
