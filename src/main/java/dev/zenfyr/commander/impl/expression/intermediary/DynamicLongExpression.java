package dev.zenfyr.commander.impl.expression.intermediary;

import com.mojang.datafixers.util.Either;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.LongExpression;
import java.util.Map;
import lombok.EqualsAndHashCode;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

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
  public long asLong(LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return expression.eval(context, parameters).getAsLong();
  }

  @Override
  public String toString() {
    return "LongExpression(expression=" + expression + ')';
  }
}
