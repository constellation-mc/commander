package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import me.melontini.commander.impl.expression.intermediaries.ConstantLongExpression;
import me.melontini.commander.impl.expression.intermediaries.DynamicLongExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@code context -> long} functions, which is encoded as either a long or an expression.
 * <p>Can be used as a substitute for {@link Codec#LONG} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
@ApiStatus.NonExtendable
public interface LongExpression
    extends ToLongFunction<LootContext>, ToLongBiFunction<LootContext, @Nullable Map<String, ?>> {

  Codec<LongExpression> CODEC = Codec.either(Codec.LONG, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(b -> DataResult.success(constant(b)), s -> Expression.parse(s)
              .map(LongExpression::of)),
          LongExpression::toSource);

  default int asInt(LootContext context) {
    return (int) this.asLong(context);
  }

  default long asLong(LootContext context) {
    return this.asLong(context, null);
  }

  default int asInt(LootContext context, @Nullable Map<String, ?> parameters) {
    return (int) this.asLong(context, parameters);
  }

  long asLong(LootContext context, @Nullable Map<String, ?> parameters);

  Either<Long, String> toSource();

  @Contract("_ -> new")
  static @NotNull LongExpression constant(long j) {
    return new ConstantLongExpression(Either.left(j), j);
  }

  static @NotNull LongExpression of(Expression expression) {
    return new DynamicLongExpression(Either.right(expression.original()), expression);
  }

  @Override
  default long applyAsLong(LootContext context, @Nullable Map<String, ?> parameters) {
    return this.asLong(context, parameters);
  }

  @Override
  default long applyAsLong(LootContext context) {
    return this.asLong(context);
  }
}
