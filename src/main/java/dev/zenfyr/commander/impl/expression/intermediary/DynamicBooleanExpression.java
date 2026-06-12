package dev.zenfyr.commander.impl.expression.intermediary;

import com.mojang.datafixers.util.Either;
import dev.zenfyr.commander.api.expression.BooleanExpression;
import dev.zenfyr.commander.api.expression.Expression;
import java.util.Map;
import lombok.EqualsAndHashCode;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class DynamicBooleanExpression implements BooleanExpression {
  @EqualsAndHashCode.Exclude
  private final Either<Boolean, String> either;

  private final Expression expression;

  public DynamicBooleanExpression(Either<Boolean, String> either, Expression expression) {
    this.either = either;
    this.expression = expression;
  }

  @Override
  public Either<Boolean, String> toSource() {
    return either;
  }

  @Override
  public boolean asBoolean(
      LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return expression.eval(context, parameters).getAsBoolean();
  }

  public String toString() {
    return "BooleanExpression(expression=" + this.expression + ')';
  }
}
