package me.melontini.commander.impl.expression.macro;

import me.melontini.commander.api.expression.BrigadierMacro;
import net.minecraft.loot.context.LootContext;

import java.util.function.Function;

public record DynamicMacro(String original, Function<LootContext, StringBuilder> start) implements BrigadierMacro {

    public String build(LootContext context) {
        return start.apply(context).toString();
    }
}
