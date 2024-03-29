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

public record AnyOfCommand(List<ConditionedCommand> commands, Optional<ConditionedCommand> then) implements Command {

    public static final Codec<AnyOfCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            ExtraCodecs.list(ConditionedCommand.CODEC).fieldOf("commands").forGetter(AnyOfCommand::commands),
            ExtraCodecs.optional("then", ConditionedCommand.CODEC).forGetter(AnyOfCommand::then)
    ).apply(data, AnyOfCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = false;
        for (ConditionedCommand command : commands()) {
            b |= command.execute(context);
        }
        if (b) {
            return then().map(conditionedCommand -> conditionedCommand.execute(context)).orElse(true);
        }
        return false;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.ANY_OF;
    }
}
