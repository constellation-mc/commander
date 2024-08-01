package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.LongExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@EqualsAndHashCode
public final class DynamicLongExpression implements LongExpression {
  @EqualsAndHashCode.Exclude
  private final Either<Long, String> either;

  private final Expression expression;

  public DynamicLongExpression(Either<Long, String> either, Expression expression) {
    this.either = either;
    this.expression = expression;
  }

  @Override
  public Either<Long, String> toSource() {
    return either;
  }

  @Override
  public long asLong(LootContext context, @Nullable Map<String, ?> parameters) {
    return expression.eval(context, parameters).getAsDecimal().longValue();
  }

  @Override
  public String toString() {
    return "LongExpression(expression=" + expression + ')';
  }
}
