package me.melontini.commander.api.expression;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
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

    Codec<BooleanExpression> CODEC = ExtraCodecs.either(Codec.BOOL, Codec.STRING).comapFlatMap((either) -> either.map(b -> DataResult.success(constant(b)), s -> Expression.parse(s).map(BooleanExpression::of)), BooleanExpression::toSource);

    boolean asBoolean(LootContext context);

    Either<Boolean, String> toSource();

    @Contract("_ -> new")
    static @NotNull BooleanExpression constant(boolean b) {
        Either<Boolean, String> either = Either.left(b);
        return new BooleanExpression() {
            @Override
            public Either<Boolean, String> toSource() {
                return either;
            }

            @Override
            public boolean asBoolean(LootContext context) {
                return b;
            }

            @Override
            public String toString() {
                return "BooleanExpression{boolean=" + b + "}";
            }
        };
    }

    @Contract("_ -> new")
    static @NotNull BooleanExpression of(Expression expression) {
        Either<Boolean, String> either = Either.right(expression.original());
        return new BooleanExpression() {
            @Override
            public Either<Boolean, String> toSource() {
                return either;
            }

            @Override
            public boolean asBoolean(LootContext context) {
                return expression.eval(context).getAsBoolean();
            }

            @Override
            public String toString() {
                return "BooleanExpression{expression=" + expression.original() + "}";
            }
        };
    }
}
