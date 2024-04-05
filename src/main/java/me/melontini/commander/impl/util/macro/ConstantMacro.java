package me.melontini.commander.impl.util.macro;

import me.melontini.commander.api.expression.BrigadierMacro;
import net.minecraft.loot.context.LootContext;

public record ConstantMacro(String original) implements BrigadierMacro {
    @Override
    public String build(LootContext context) {
        return original();
    }
}
