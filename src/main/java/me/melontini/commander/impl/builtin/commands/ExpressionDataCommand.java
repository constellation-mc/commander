package me.melontini.commander.impl.builtin.commands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public record ExpressionDataCommand(StoreDataCommand.Target target, Selector.Conditioned selector, String key, Expression expression) implements StoreDataCommand {

    public static final MapCodec<ExpressionDataCommand> CODEC = RecordCodecBuilder.mapCodec(data -> data.group(
            ExtraCodecs.enumCodec(StoreDataCommand.Target.class).fieldOf("target").forGetter(ExpressionDataCommand::target),
            Selector.CODEC.fieldOf("selector").forGetter(ExpressionDataCommand::selector),
            Codec.STRING.fieldOf("key").forGetter(ExpressionDataCommand::key),
            Expression.CODEC.fieldOf("expression").forGetter(ExpressionDataCommand::expression)
    ).apply(data, ExpressionDataCommand::new));

    @Override
    public CommandType type() {
        return BuiltInCommands.STORE_EXP_DATA;
    }

    @Override
    public NbtElement asElement(EventContext context) {
        var r = expression.apply(context.lootContext());

        if (r.isDecimal()) return NbtDouble.of(r.getAsDecimal().doubleValue());
        if (r.isString()) return NbtString.of(r.getAsString());

        throw new IllegalStateException("Persistent data must be a number or a string!");
    }
}
