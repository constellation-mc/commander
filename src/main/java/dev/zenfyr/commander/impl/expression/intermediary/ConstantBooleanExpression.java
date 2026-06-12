package dev.zenfyr.commander.impl.expression.intermediary;

import com.mojang.datafixers.util.Either;
import dev.zenfyr.commander.api.expression.BooleanExpression;
import dev.zenfyr.commander.api.expression.Expression;
import java.util.Map;
import lombok.EqualsAndHashCode;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class ConstantBooleanExpression implements BooleanExpression {

  public static final ConstantBooleanExpression TRUE =
      new ConstantBooleanExpression(Either.left(true), true);
  public static final ConstantBooleanExpression FALSE =
      new ConstantBooleanExpression(Either.left(false), false);

  @EqualsAndHashCode.Exclude
  private final Either<Boolean, String> either;

  private final boolean value;

  private ConstantBooleanExpression(Either<Boolean, String> either, boolean value) {
    this.either = either;
    this.value = value;
  }

  @Override
  public Either<Boolean, String> toSource() {
    return either;
  }

  @Override
  public boolean asBoolean(
      LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return value;
  }

  public String toString() {
    return "BooleanExpression(value=" + this.value + ')';
  }
}
