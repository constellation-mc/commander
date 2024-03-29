package me.melontini.commander.builtin.commands.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.event.EventContext;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AllOfCommand(List<ConditionedCommand> commands, Optional<ConditionedCommand> then) implements Command {

    public static final Codec<AllOfCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            ExtraCodecs.list(ConditionedCommand.CODEC).fieldOf("commands").forGetter(AllOfCommand::commands),
            ExtraCodecs.optional("then", ConditionedCommand.CODEC).forGetter(AllOfCommand::then)
    ).apply(data, AllOfCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = true;
        for (ConditionedCommand command : commands()) {
            b &= command.execute(context);
        }
        if (b) {
            return then().map(conditionedCommand -> conditionedCommand.execute(context)).orElse(true);
        }
        return false;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.ALL_OF;
    }
}
