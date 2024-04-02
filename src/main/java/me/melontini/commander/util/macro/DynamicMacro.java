package me.melontini.commander.util.macro;

import me.melontini.commander.event.EventContext;
import net.minecraft.loot.context.LootContext;

import java.util.function.Function;

public record DynamicMacro(String original, Function<LootContext, StringBuilder> start) implements BrigadierMacro {

    public String build(EventContext context) {
        return start.apply(context.lootContext()).toString();
    }
}
