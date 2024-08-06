package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import java.util.Map;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.LongExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class ConstantLongExpression implements LongExpression {
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
  public long asLong(LootContext context, @Nullable Map<String, ?> parameters) {
    return value;
  }

  @Override
  public String toString() {
    return "LongExpression(value=" + value + ')';
  }
}
