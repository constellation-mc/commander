package me.melontini.commander.util.macro;

import lombok.RequiredArgsConstructor;
import me.melontini.commander.util.functions.ToDoubleFunction;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class MacroContainer {

    private final Map<String, ToDoubleFunction<ServerCommandSource>> arithmeticFunctions;
    private final Map<String, Function<ServerCommandSource, String>> stringFunctions;

    public boolean isArithmetic(String field) {
        return arithmeticFunctions.containsKey(field);
    }

    public boolean contains(String field) {
        return arithmeticFunctions.containsKey(field) || stringFunctions.containsKey(field);
    }

    public Function<ServerCommandSource, String> ofString(String field) {
        return stringFunctions.get(field);
    }

    public ToDoubleFunction<ServerCommandSource> ofDouble(String field) {
        return arithmeticFunctions.get(field);
    }
}
