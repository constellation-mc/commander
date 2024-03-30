package me.melontini.commander.builtin.commands.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.event.EventContext;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.collection.WeightedList;

public record RandomCommand(WeightedList<ConditionedCommand> commands, int rolls) implements Command {

    public static final Codec<RandomCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
                ExtraCodecs.weightedList(ConditionedCommand.CODEC).fieldOf("commands").forGetter(RandomCommand::commands),
                ExtraCodecs.optional("rolls", Codec.INT, 1).forGetter(RandomCommand::rolls)
        ).apply(data, RandomCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = false;
        for (int i = 0; i < rolls(); i++) {
            var itr = this.commands().shuffle().iterator();
            if (itr.hasNext()) b |= itr.next().execute(context);
        }
        return b;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.RANDOM;
    }
}