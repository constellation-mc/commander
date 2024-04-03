package me.melontini.commander.impl.util.macro;

import me.melontini.commander.api.event.EventContext;

public record ConstantMacro(String original) implements BrigadierMacro {
    @Override
    public String build(EventContext context) {
        return original();
    }
}
