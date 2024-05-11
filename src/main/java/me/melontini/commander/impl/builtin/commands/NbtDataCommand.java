package me.melontini.commander.impl.builtin.commands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.util.NbtCodecs;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.nbt.NbtElement;

public record NbtDataCommand(Target target, Selector.Conditioned selector, String key, NbtElement element) implements StoreDataCommand {

    public static final MapCodec<NbtDataCommand> CODEC = RecordCodecBuilder.mapCodec(data -> data.group(
            ExtraCodecs.enumCodec(StoreDataCommand.Target.class).fieldOf("target").forGetter(NbtDataCommand::target),
            Selector.CODEC.fieldOf("selector").forGetter(NbtDataCommand::selector),
            Codec.STRING.fieldOf("key").forGetter(NbtDataCommand::key),
            NbtCodecs.PRIMITIVE_CODEC.fieldOf("element").forGetter(NbtDataCommand::element)
    ).apply(data, NbtDataCommand::new));

    @Override
    public NbtElement asElement(EventContext context) {
        return element;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.STORE_NBT_DATA;
    }
}
