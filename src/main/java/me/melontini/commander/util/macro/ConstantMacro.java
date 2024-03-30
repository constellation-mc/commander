package me.melontini.commander.util.macro;

import me.melontini.commander.event.EventContext;

public record ConstantMacro(String original) implements BrigadierMacro {
    @Override
    public String build(EventContext context) {
        return original();
    }
}
