package me.melontini.commander.util.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import me.melontini.commander.util.functions.ToDoubleFunction;
import me.melontini.commander.util.macro.PatternParser;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContext;

public interface Arithmetica extends ToDoubleFunction<LootContext> {

    Codec<Arithmetica> CODEC = ExtraCodecs.either(Codec.DOUBLE, Codec.STRING).comapFlatMap(PatternParser::parseArithmetica, Arithmetica::toSource);

    default long asLong(LootContext context) {
        return (long) this.apply(context);
    }

    default int asInt(LootContext context) {
        return (int) this.apply(context);
    }

    default float asFloat(LootContext context) {
        return (float) this.apply(context);
    }

    default double asDouble(LootContext context) {
        return this.apply(context);
    }

    Either<Double, String> toSource();

    static Arithmetica constant(double d) {
        return new Arithmetica() {
            @Override
            public Either<Double, String> toSource() {
                return Either.left(d);
            }

            @Override
            public double apply(LootContext context) {
                return d;
            }
        };
    }

    static Arithmetica of(ToDoubleFunction<LootContext> function, String expression) {
        return new Arithmetica() {
            @Override
            public Either<Double, String> toSource() {
                return Either.right(expression);
            }

            @Override
            public double apply(LootContext context) {
                return function.apply(context);
            }
        };
    }
}
