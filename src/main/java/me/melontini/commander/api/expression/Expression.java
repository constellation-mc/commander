package me.melontini.commander.api.expression;

import com.ezylang.evalex.data.types.*;
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
import org.jetbrains.annotations.NotNull;
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
  Result eval(LootContext context, @Nullable Map<String, ?> parameters);

  String original();

  interface Result {

    static Result convert(Object o) {
      return (Result) (Object) ReflectiveValueConverter.convert(o);
    }

    static Result convert(BigDecimal decimal) {
      return (Result) (Object) NumberValue.of(decimal);
    }

    static Result convert(boolean decimal) {
      return (Result) (Object) BooleanValue.of(decimal);
    }

    static Result convert(String string) {
      return (Result) (Object) StringValue.of(string);
    }

    static Result convert(Instant instant) {
      return (Result) (Object) DateTimeValue.of(instant);
    }

    static Result convert(Duration duration) {
      return (Result) (Object) DurationValue.of(duration);
    }

    @NotNull BigDecimal getAsDecimal();

    boolean getAsBoolean();

    @NotNull String getAsString();

    @NotNull Instant getAsInstant();

    @NotNull Duration getAsDuration();

    boolean isDecimalValue();

    boolean isBooleanValue();

    boolean isStringValue();

    boolean isInstantValue();

    boolean isDurationValue();

    boolean isNullValue();

    @Nullable Object getValue();
  }
}
