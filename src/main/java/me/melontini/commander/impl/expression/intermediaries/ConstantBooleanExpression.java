package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import java.util.Map;
import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.BooleanExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class ConstantBooleanExpression implements BooleanExpression {
  @EqualsAndHashCode.Exclude
  private final Either<Boolean, String> either;

  private final boolean value;

  public ConstantBooleanExpression(Either<Boolean, String> either, boolean value) {
    this.either = either;
    this.value = value;
  }

  @Override
  public Either<Boolean, String> toSource() {
    return either;
  }

  @Override
  public boolean asBoolean(LootContext context, @Nullable Map<String, ?> parameters) {
    return value;
  }

  public String toString() {
    return "BooleanExpression(value=" + this.value + ')';
  }
}
