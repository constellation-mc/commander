package me.melontini.commander.impl.builtin.commands.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AllOfCommand(List<Command.Conditioned> commands, Optional<Command.Conditioned> then) implements Command {

    public static final Codec<AllOfCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            ExtraCodecs.list(Command.CODEC).fieldOf("commands").forGetter(AllOfCommand::commands),
            ExtraCodecs.optional("then", Command.CODEC).forGetter(AllOfCommand::then)
    ).apply(data, AllOfCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = true;
        for (Conditioned command : commands()) {
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
