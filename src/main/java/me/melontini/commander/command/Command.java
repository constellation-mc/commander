package me.melontini.commander.command;

import me.melontini.commander.event.EventContext;


public interface Command {
    boolean execute(EventContext context);
    CommandType type();
}
