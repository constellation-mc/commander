package me.melontini.commander.api.command;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.impl.command.ConditionedCommand;


public interface Command {

    Codec<Conditioned> CODEC = (Codec<Conditioned>) ConditionedCommand.CODEC;

    boolean execute(EventContext context);
    CommandType type();

    default DataResult<Void> validate(EventType type) {
        return DataResult.success(null);
    }

    interface Conditioned {
        boolean execute(EventContext context);
        DataResult<Void> validate(EventType type);
    }
}
