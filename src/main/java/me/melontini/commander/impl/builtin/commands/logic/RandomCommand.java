package me.melontini.commander.impl.builtin.commands.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.collection.WeightedList;

public record RandomCommand(WeightedList<Command.Conditioned> commands, Arithmetica rolls) implements Command {

    public static final Codec<RandomCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
                ExtraCodecs.weightedList(Command.CODEC).fieldOf("commands").forGetter(RandomCommand::commands),
                ExtraCodecs.optional("rolls", Arithmetica.CODEC, Arithmetica.constant(1)).forGetter(RandomCommand::rolls)
        ).apply(data, RandomCommand::new));

    @Override
    public boolean execute(EventContext context) {
        boolean b = false;
        int rolls = rolls().asInt(context.lootContext());
        for (int i = 0; i < rolls; i++) {
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
