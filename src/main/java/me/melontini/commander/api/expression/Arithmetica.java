package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToDoubleFunction;

/**
 * A simple {@code context -> double} functions, which is encoded as either a double or an expression.
 * <p>Can be used as a substitute for {@link Codec#DOUBLE} if {@link LootContext} is available</p>
 *
 * @see Expression
 */
public interface Arithmetica extends ToDoubleFunction<LootContext> {

    Codec<Arithmetica> CODEC = ExtraCodecs.either(Codec.DOUBLE, Codec.STRING).comapFlatMap(EvalUtils::parseEither, Arithmetica::toSource);

    default long asLong(LootContext context) {
        return (long) this.applyAsDouble(context);
    }

    default int asInt(LootContext context) {
        return (int) this.applyAsDouble(context);
    }

    default float asFloat(LootContext context) {
        return (float) this.applyAsDouble(context);
    }

    default double asDouble(LootContext context) {
        return this.applyAsDouble(context);
    }

    Either<Double, String> toSource();

    @Contract("_ -> new")
    static @NotNull Arithmetica constant(double d) {
        Either<Double, String> either = Either.left(d);
        return new Arithmetica() {
            @Override
            public Either<Double, String> toSource() {
                return either;
            }

            @Override
            public double applyAsDouble(LootContext context) {
                return d;
            }
        };
    }

    @Contract("_, _ -> new")
    static @NotNull Arithmetica of(ToDoubleFunction<LootContext> function, String expression) {
        Either<Double, String> either = Either.right(expression);
        return new Arithmetica() {
            @Override
            public Either<Double, String> toSource() {
                return either;
            }

            @Override
            public double applyAsDouble(LootContext context) {
                return function.applyAsDouble(context);
            }
        };
    }
}
