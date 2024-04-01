package me.melontini.commander.util.macro;

import lombok.RequiredArgsConstructor;
import me.melontini.commander.util.functions.ToDoubleBiFunction;
import me.melontini.commander.util.functions.ToDoubleFunction;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public class MacroContainer {

    private final Map<String, ToDoubleFunction<ServerCommandSource>> arithmeticFunctions;
    private final Map<String, Function<ServerCommandSource, String>> stringFunctions;

    private final Map<String, ArithmeticEntry<?>> dynamicArithmeticFunctions;
    private final Map<String, StringEntry<?>> dynamicStringFunctions;

    public boolean isArithmetic(String field) {
        return arithmeticFunctions.containsKey(field) || dynamicArithmeticFunctions.containsKey(field);
    }

    public boolean isDynamic(String field) {
        return dynamicArithmeticFunctions.containsKey(field) || dynamicStringFunctions.containsKey(field);
    }

    public boolean contains(String field) {
        return arithmeticFunctions.containsKey(field) || stringFunctions.containsKey(field)
                || dynamicArithmeticFunctions.containsKey(field) || dynamicStringFunctions.containsKey(field);
    }

    public Function<ServerCommandSource, String> ofString(String field) {
        return stringFunctions.get(field);
    }

    public ToDoubleFunction<ServerCommandSource> ofDouble(String field) {
        return arithmeticFunctions.get(field);
    }

    public StringEntry<Object> ofDynamicString(String field) {
        return (StringEntry<Object>) dynamicStringFunctions.get(field);
    }

    public ArithmeticEntry<Object> ofDynamicDouble(String field) {
        return (ArithmeticEntry<Object>) dynamicArithmeticFunctions.get(field);
    }

    public record ArithmeticEntry<T>(Function<String, T> transformer, ToDoubleBiFunction<T, ServerCommandSource> arithmetic) { }
    public record StringEntry<T>(Function<String, T> transformer, BiFunction<T, ServerCommandSource, String> string) { }
}
