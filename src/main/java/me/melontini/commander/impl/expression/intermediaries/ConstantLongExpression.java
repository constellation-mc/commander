package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.LongExpression;
import net.minecraft.loot.context.LootContext;

@EqualsAndHashCode
public class ConstantLongExpression implements LongExpression {
  @EqualsAndHashCode.Exclude
  private final Either<Long, String> either;

  private final long value;

  public ConstantLongExpression(Either<Long, String> either, long value) {
    this.either = either;
    this.value = value;
  }

  @Override
  public Either<Long, String> toSource() {
    return either;
  }

  @Override
  public long applyAsLong(LootContext context) {
    return value;
  }

  @Override
  public String toString() {
    return "LongExpression(value=" + value + ')';
  }
}
