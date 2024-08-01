package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import me.melontini.commander.impl.expression.intermediaries.ConstantArithmetica;
import me.melontini.commander.impl.expression.intermediaries.DynamicArithmetica;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@code context -> double} functions, which is encoded as either a double or an expression.
 * <p>Can be used as a substitute for {@link Codec#DOUBLE} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
public interface Arithmetica
    extends ToDoubleFunction<LootContext>,
        ToDoubleBiFunction<LootContext, @Nullable Map<String, ?>> {

  Codec<Arithmetica> CODEC = Codec.either(Codec.DOUBLE, Codec.STRING)
      .comapFlatMap(
          (either) -> either.map(
              b -> DataResult.success(constant(b)), s -> Expression.parse(s).map(Arithmetica::of)),
          Arithmetica::toSource);

  default long asLong(LootContext context) {
    return (long) this.asDouble(context);
  }

  default int asInt(LootContext context) {
    return (int) this.asDouble(context);
  }

  default float asFloat(LootContext context) {
    return (float) this.asDouble(context);
  }

  default double asDouble(LootContext context) {
    return this.asDouble(context, null);
  }

  default long asLong(LootContext context, @Nullable Map<String, ?> parameters) {
    return (long) this.asDouble(context, parameters);
  }

  default int asInt(LootContext context, @Nullable Map<String, ?> parameters) {
    return (int) this.asDouble(context, parameters);
  }

  default float asFloat(LootContext context, @Nullable Map<String, ?> parameters) {
    return (float) this.asDouble(context, parameters);
  }

  double asDouble(LootContext context, @Nullable Map<String, ?> parameters);

  Either<Double, String> toSource();

  @Contract("_ -> new")
  static @NotNull Arithmetica constant(double d) {
    Either<Double, String> either = Either.left(d);
    return new ConstantArithmetica(either, d);
  }

  static @NotNull Arithmetica of(Expression expression) {
    Either<Double, String> either = Either.right(expression.original());
    return new DynamicArithmetica(either, expression);
  }

  @Override
  default double applyAsDouble(LootContext context, @Nullable Map<String, ?> parameters) {
    return this.asDouble(context, parameters);
  }

  @Override
  default double applyAsDouble(LootContext context) {
    return this.asDouble(context);
  }
}
