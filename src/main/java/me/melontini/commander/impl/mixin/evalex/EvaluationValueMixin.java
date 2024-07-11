package me.melontini.commander.impl.mixin.evalex;

import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.api.expression.Expression;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Mixin(value = EvaluationValue.class, remap = false)
public abstract class EvaluationValueMixin implements Expression.Result {

    @Shadow public abstract BigDecimal getNumberValue();
    @Shadow public abstract Boolean getBooleanValue();
    @Shadow public abstract String getStringValue();
    @Shadow public abstract Instant getDateTimeValue();
    @Shadow public abstract Duration getDurationValue();
    @Shadow public abstract boolean isNumberValue();
    @Shadow public abstract boolean isDateTimeValue();

    @Override
    public BigDecimal getAsDecimal() {
        return getNumberValue();
    }

    @Override
    public boolean getAsBoolean() {
        return getBooleanValue();
    }

    @Override
    public String getAsString() {
        return getStringValue();
    }

    @Override
    public Instant getAsInstant() {
        return getDateTimeValue();
    }

    @Override
    public Duration getAsDuration() {
        return getDurationValue();
    }

    @Override
    public boolean isDecimalValue() {
        return isNumberValue();
    }

    @Override
    public boolean isInstantValue() {
        return isDateTimeValue();
    }
}
