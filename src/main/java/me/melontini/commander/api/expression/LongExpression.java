package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.ToLongFunction;
import me.melontini.commander.impl.expression.intermediaries.ConstantLongExpression;
import me.melontini.commander.impl.expression.intermediaries.DynamicLongExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface LongExpression extends ToLongFunction<LootContext> {

  Codec<LongExpression> CODEC = Codec.either(Codec.LONG, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(b -> DataResult.success(constant(b)), s -> Expression.parse(s)
              .map(LongExpression::of)),
          LongExpression::toSource);

  default int asInt(LootContext context) {
    return (int) this.applyAsLong(context);
  }

  default long asLong(LootContext context) {
    return this.applyAsLong(context);
  }

  Either<Long, String> toSource();

  @Contract("_ -> new")
  static @NotNull LongExpression constant(long j) {
    Either<Long, String> either = Either.left(j);
    return new ConstantLongExpression(either, j);
  }

  static @NotNull LongExpression of(Expression expression) {
    Either<Long, String> either = Either.right(expression.original());
    return new DynamicLongExpression(either, expression);
  }
}
