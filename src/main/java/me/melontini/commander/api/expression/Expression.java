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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Expression extends Function<LootContext, Expression.Result> {

  Codec<Expression> CODEC = Codec.STRING.comapFlatMap(Expression::parse, Expression::original);

  static DataResult<Expression> parse(String expression) {
    return (DataResult<Expression>) (Object) EvalUtils.parseExpression(expression);
  }

  default Result eval(LootContext context) {
    return this.eval(context, null);
  }

  /**
   * Evaluates expressions with additional parameters.
   * Parameters must be consistent, if something is unavailable - use {@link Result#NULL}.
   * Otherwise, expressions could start failing and using the {@code ?} operator will be impossible.
   *
   * @return The evaluation {@link Result}.
   * @see #eval(LootContext)
   */
  Result eval(LootContext context, @Nullable Map<String, ?> parameters);

  String original();

  interface Result {

    Result NULL = (Result) (Object) NullValue.of();

    static Result convert(Object o) {
      return (Result) (Object) ReflectiveValueConverter.convert(o);
    }

    static Result convert(BigDecimal decimal) {
      return (Result) (Object) NumberValue.of(decimal);
    }

    static Result convert(boolean bool) {
      return (Result) (Object) BooleanValue.of(bool);
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

  @Override
  default Result apply(LootContext context) {
    return this.eval(context);
  }
}
