package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.melontini.commander.impl.expression.intermediaries.ConstantBooleanExpression;
import me.melontini.commander.impl.expression.intermediaries.DynamicBooleanExpression;
import me.melontini.commander.impl.expression.intermediaries.NegatedBooleanExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@code context -> boolean} functions, which is encoded as either a boolean or an expression.
 * <p>Can be used as a substitute for {@link Codec#BOOL} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
@ApiStatus.NonExtendable
public interface BooleanExpression
    extends Predicate<LootContext>, BiPredicate<LootContext, @Nullable Map<String, ?>> {

  Codec<BooleanExpression> CODEC = Codec.either(Codec.BOOL, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(b -> DataResult.success(constant(b)), s -> Expression.parse(s)
              .map(BooleanExpression::of)),
          BooleanExpression::toSource);

  default boolean asBoolean(LootContext context) {
    return this.asBoolean(context, null);
  }

  boolean asBoolean(LootContext context, @Nullable Map<String, ?> parameters);

  Either<Boolean, String> toSource();

  @Contract("_ -> new")
  static @NotNull BooleanExpression constant(boolean b) {
    return b ? ConstantBooleanExpression.TRUE : ConstantBooleanExpression.FALSE;
  }

  @Contract("_ -> new")
  static @NotNull BooleanExpression of(Expression expression) {
    return new DynamicBooleanExpression(Either.right(expression.original()), expression);
  }

  @Override
  default boolean test(LootContext context, @Nullable Map<String, ?> parameters) {
    return this.asBoolean(context, parameters);
  }

  @Override
  default boolean test(LootContext context) {
    return this.asBoolean(context);
  }

  @Override
  default @NotNull BooleanExpression negate() {
    return new NegatedBooleanExpression(this);
  }
}
