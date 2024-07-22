package me.melontini.commander.impl.expression.macro;

import me.melontini.commander.api.expression.BrigadierMacro;
import net.minecraft.loot.context.LootContext;

import java.util.Map;
import java.util.function.BiFunction;

public record DynamicMacro(String original, BiFunction<LootContext, Map<String, Object>, StringBuilder> start) implements BrigadierMacro {

    @Override
    public String build(LootContext context, Map<String, Object> params) {
        return start.apply(context, params).toString();
    }
}
