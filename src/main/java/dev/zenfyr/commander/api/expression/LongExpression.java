package dev.zenfyr.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.zenfyr.commander.impl.expression.intermediary.ConstantLongExpression;
import dev.zenfyr.commander.impl.expression.intermediary.DynamicLongExpression;
import java.util.Map;
import net.minecraft.world.level.storage.loot.LootContext;
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
public interface LongExpression {

  Codec<LongExpression> CODEC = Codec.either(Codec.LONG, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(
              b -> DataResult.success(constant(b)),
              s -> Expression.parse(s).map(LongExpression::of)),
          LongExpression::toSource);

  default int asInt(LootContext context) {
    return (int) this.asLong(context);
  }

  default long asLong(LootContext context) {
    return this.asLong(context, null);
  }

  default int asInt(LootContext context, @Nullable Map<String, Expression.Result> parameters) {
    return (int) this.asLong(context, parameters);
  }

  long asLong(LootContext context, @Nullable Map<String, Expression.Result> parameters);

  Either<Long, String> toSource();

  @Contract("_ -> new")
  static @NotNull LongExpression constant(long j) {
    return new ConstantLongExpression(Either.left(j), j);
  }

  static @NotNull LongExpression of(Expression expression) {
    return new DynamicLongExpression(Either.right(expression.original()), expression);
  }
}
