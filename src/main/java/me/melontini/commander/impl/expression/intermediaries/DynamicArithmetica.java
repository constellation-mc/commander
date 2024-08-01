package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.api.expression.Expression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@EqualsAndHashCode
public final class DynamicArithmetica implements Arithmetica {
  @EqualsAndHashCode.Exclude
  private final Either<Double, String> either;

  private final Expression expression;

  public DynamicArithmetica(Either<Double, String> either, Expression expression) {
    this.either = either;
    this.expression = expression;
  }

  @Override
  public Either<Double, String> toSource() {
    return either;
  }

  @Override
  public double asDouble(LootContext context, @Nullable Map<String, ?> parameters) {
    return expression.eval(context, parameters).getAsDecimal().doubleValue();
  }

  public String toString() {
    return "Arithmetica(expression=" + this.expression + ')';
  }
}
