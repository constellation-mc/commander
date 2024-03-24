package me.melontini.commander.command.builtin.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.command.builtin.BuiltInCommands;
import me.melontini.commander.event.EventContext;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record DefaultedCommand(List<ConditionedCommand> commands, Optional<ConditionedCommand> then) implements Command {

    public static final Codec<DefaultedCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            ExtraCodecs.list(ConditionedCommand.CODEC).fieldOf("commands").forGetter(DefaultedCommand::commands),
            ExtraCodecs.optional("then", ConditionedCommand.CODEC).forGetter(DefaultedCommand::then)
    ).apply(data, DefaultedCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = false;
        for (ConditionedCommand command : commands()) {
            b |= command.execute(context);
        }
        if (!b) {
            return then().map(conditionedCommand -> conditionedCommand.execute(context)).orElse(false);
        }
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.DEFAULTED;
    }
}