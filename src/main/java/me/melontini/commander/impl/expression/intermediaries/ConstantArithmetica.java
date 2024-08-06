package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import java.util.Map;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.Arithmetica;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class ConstantArithmetica implements Arithmetica {
  @EqualsAndHashCode.Exclude
  private final Either<Double, String> either;

  private final double value;

  public ConstantArithmetica(Either<Double, String> either, double value) {
    this.either = either;
    this.value = value;
  }

  @Override
  public Either<Double, String> toSource() {
    return either;
  }

  @Override
  public double asDouble(LootContext context, @Nullable Map<String, ?> parameters) {
    return value;
  }

  public String toString() {
    return "Arithmetica(value=" + this.value + ')';
  }
}
