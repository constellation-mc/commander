package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface Expression extends Function<LootContext, Expression.Result> {

  Codec<Expression> CODEC = Codec.STRING.comapFlatMap(Expression::parse, Expression::original);

  static DataResult<Expression> parse(String expression) {
    return (DataResult<Expression>) (Object) EvalUtils.parseExpression(expression);
  }

  @Override
  default Result apply(LootContext context) {
    return this.eval(context);
  }

  default Result eval(LootContext context) {
    return this.eval(context, null);
  }

  @ApiStatus.Experimental
  Result eval(LootContext context, @Nullable Map<String, Object> parameters);

  String original();

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
