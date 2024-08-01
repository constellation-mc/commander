package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.expression.intermediaries.ConstantBooleanExpression;
import me.melontini.commander.impl.expression.intermediaries.DynamicBooleanExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@code context -> boolean} functions, which is encoded as either a boolean or an expression.
 * <p>Can be used as a substitute for {@link Codec#BOOL} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
public interface BooleanExpression {

  Codec<BooleanExpression> CODEC = Codec.either(Codec.BOOL, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(b -> DataResult.success(constant(b)), s -> Expression.parse(s)
              .map(BooleanExpression::of)),
          BooleanExpression::toSource);

  boolean asBoolean(LootContext context);

  Either<Boolean, String> toSource();

  @Contract("_ -> new")
  static @NotNull BooleanExpression constant(boolean b) {
    Either<Boolean, String> either = Either.left(b);
    return new ConstantBooleanExpression(either, b);
  }

  @Contract("_ -> new")
  static @NotNull BooleanExpression of(Expression expression) {
    Either<Boolean, String> either = Either.right(expression.original());
    return new DynamicBooleanExpression(either, expression);
  }
}
