package dev.zenfyr.commander.impl.mixin.evalex;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.number.NumberValue;
import dev.zenfyr.commander.api.expression.Expression;
import java.time.Duration;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EvaluationValue.class, remap = false)
public abstract class EvaluationValueMixin implements Expression.Result {

  @Shadow
  public abstract NumberValue getNumberValue();

  @Shadow
  public abstract Boolean getBooleanValue();

  @Shadow
  public abstract String getStringValue();

  @Shadow
  public abstract Instant getDateTimeValue();

  @Shadow
  public abstract Duration getDurationValue();

  @Shadow
  public abstract boolean isNumberValue();

  @Shadow
  public abstract boolean isDateTimeValue();

  @Shadow
  public abstract Object getValue();

  @Override
  public long getAsLong() {
    return getNumberValue().longValue();
  }

  @Override
  public double getAsDouble() {
    return getNumberValue().doubleValue();
  }

  @Override
  public boolean getAsBoolean() {
    return getBooleanValue();
  }

  @Override
  @Nullable public String getAsString() {
    return getStringValue();
  }

  @Override
  @Nullable public Instant getAsInstant() {
    return getDateTimeValue();
  }

  @Override
  @Nullable public Duration getAsDuration() {
    return getDurationValue();
  }

  @Override
  public boolean isInstantValue() {
    return isDateTimeValue();
  }
}
