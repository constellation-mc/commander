package me.melontini.commander.impl.builtin.commands.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AnyOfCommand(List<Command.Conditioned> commands, Optional<Command.Conditioned> then) implements Command {

    public static final MapCodec<AnyOfCommand> CODEC = RecordCodecBuilder.mapCodec(data -> data.group(
            ExtraCodecs.list(Command.CODEC.codec()).fieldOf("commands").forGetter(AnyOfCommand::commands),
            ExtraCodecs.optional("then", Command.CODEC.codec()).forGetter(AnyOfCommand::then)
    ).apply(data, AnyOfCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = false;
        for (Command.Conditioned command : commands()) {
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
