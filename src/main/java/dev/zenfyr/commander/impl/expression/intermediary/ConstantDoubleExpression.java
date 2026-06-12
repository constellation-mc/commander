package dev.zenfyr.commander.impl.expression.intermediary;

import com.mojang.datafixers.util.Either;
import dev.zenfyr.commander.api.expression.DoubleExpression;
import dev.zenfyr.commander.api.expression.Expression;
import java.util.Map;
import lombok.EqualsAndHashCode;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class ConstantDoubleExpression implements DoubleExpression {
  @EqualsAndHashCode.Exclude
  private final Either<Double, String> either;

  private final double value;

  public ConstantDoubleExpression(Either<Double, String> either, double value) {
    this.either = either;
    this.value = value;
  }

  @Override
  public Either<Double, String> toSource() {
    return either;
  }

  @Override
  public double asDouble(LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return value;
  }

  public String toString() {
    return "DoubleExpression(value=" + this.value + ')';
  }
}
