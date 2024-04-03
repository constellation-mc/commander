package me.melontini.commander.impl.util;

import com.google.common.collect.ImmutableSet;
import me.melontini.dark_matter.api.base.util.MathUtil;
import net.minecraft.util.math.MathHelper;
import net.objecthunter.exp4j.function.Function;

import java.util.Set;

public class StdFunctions {

    public static final Set<Function> FUNCTIONS = ImmutableSet.<Function>builder()
            .add(func("round", 1, args -> Math.round(args[0])))
            .add(func("random", 2, args -> MathUtil.nextDouble(args[0], args[1])))
            .add(func("clamp", 3, args -> MathHelper.clamp(args[0], args[1], args[2])))
            .add(func("min", 2, args -> Math.min(args[0], args[1])))
            .add(func("max", 2, args -> Math.max(args[0], args[1])))
            .add(func("lerp", 3, args -> MathHelper.lerp(args[0], args[1], args[2])))
            .build();

    private static Function func(String name, int args, Calc calc) {
        return new Function(name, args) {
            @Override
            public double apply(double... args) {
                return calc.calc(args);
            }
        };
    }

    private interface Calc {
        double calc(double... args);
    }
}
