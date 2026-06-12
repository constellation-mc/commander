package dev.zenfyr.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.zenfyr.commander.impl.expression.intermediary.ConstantDoubleExpression;
import dev.zenfyr.commander.impl.expression.intermediary.DynamicDoubleExpression;
import java.util.Map;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@code context -> double} functions, which is encoded as either a double or an expression.
 * <p>Can be used as a substitute for {@link Codec#DOUBLE} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
@ApiStatus.NonExtendable
public interface DoubleExpression {

  Codec<DoubleExpression> CODEC = Codec.either(Codec.DOUBLE, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(
              b -> DataResult.success(constant(b)),
              s -> Expression.parse(s).map(DoubleExpression::of)),
          DoubleExpression::toSource);

  default float asFloat(LootContext context) {
    return (float) this.asDouble(context);
  }

  default double asDouble(LootContext context) {
    return this.asDouble(context, null);
  }

  default float asFloat(LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return (float) this.asDouble(context, parameters);
  }

  double asDouble(LootContext context, @Nullable Map<String, Expression.Result> parameters);

  Either<Double, String> toSource();

  @Contract("_ -> new")
  static @NotNull DoubleExpression constant(double d) {
    return new ConstantDoubleExpression(Either.left(d), d);
  }

  static @NotNull DoubleExpression of(Expression expression) {
    return new DynamicDoubleExpression(Either.right(expression.original()), expression);
  }
}
