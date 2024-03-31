package me.melontini.commander.util.macro;

import me.melontini.commander.event.EventContext;

import java.util.function.Function;

public record DynamicMacro(String original, Function<EventContext, StringBuilder> start) implements BrigadierMacro {

    public String build(EventContext context) {
        return start.apply(context).toString();
    }
}
