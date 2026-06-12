package dev.zenfyr.commander.api.expression;

import com.ezylang.evalex.data.EvaluationValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Expression {

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
  @NotNull Result eval(LootContext context, @Nullable Map<String, Result> parameters);

  /**
   * @return The original expression string.
   */
  String original();

  @ApiStatus.NonExtendable
  interface Result {

    Result NULL = (Result) (Object) EvaluationValue.NULL_VALUE;

    static Result convert(Object o) {
      return (Result)
          (Object) com.ezylang.evalex.Expression.convertValue(o, EvalUtils.CONFIGURATION);
    }

    static Result convert(int value) {
      return (Result) (Object) EvaluationValue.numberValue(value);
    }

    static Result convert(long value) {
      return (Result) (Object) EvaluationValue.numberValue(value);
    }

    static Result convert(float value) {
      return (Result) (Object) EvaluationValue.numberValue(value);
    }

    static Result convert(double value) {
      return (Result) (Object) EvaluationValue.numberValue(value);
    }

    static Result convert(boolean bool) {
      return (Result) (Object) EvaluationValue.booleanValue(bool);
    }

    static Result convert(String string) {
      return (Result) (Object) EvaluationValue.stringValue(string);
    }

    static Result convert(Instant instant) {
      return (Result) (Object) EvaluationValue.dateTimeValue(instant);
    }

    static Result convert(Duration duration) {
      return (Result) (Object) EvaluationValue.durationValue(duration);
    }

    default int getAsInt() {
      return (int) this.getAsLong();
    }

    long getAsLong();

    double getAsDouble();

    default float getAsFloat() {
      return (float) this.getAsDouble();
    }

    boolean getAsBoolean();

    String getAsString();

    Instant getAsInstant();

    Duration getAsDuration();

    boolean isNumberValue();

    boolean isBooleanValue();

    boolean isStringValue();

    boolean isInstantValue();

    boolean isDurationValue();

    boolean isNullValue();

    Object getValue();
  }
}
