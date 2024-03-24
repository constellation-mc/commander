package me.melontini.commander.command;

import com.mojang.serialization.DataResult;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;


public interface Command {
    boolean execute(EventContext context);
    CommandType type();

    default DataResult<Void> validate(EventType type) {
        return DataResult.success(null);
    }
}
