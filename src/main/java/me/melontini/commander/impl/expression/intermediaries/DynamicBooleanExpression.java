package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.BooleanExpression;
import me.melontini.commander.api.expression.Expression;
import net.minecraft.loot.context.LootContext;

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
  public boolean asBoolean(LootContext context) {
    return expression.eval(context).getAsBoolean();
  }

  public String toString() {
    return "BooleanExpression(expression=" + this.expression + ')';
  }
}
