package me.melontini.commander.builtin.commands.action;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;
import net.minecraft.util.dynamic.Codecs;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class CancelCommand implements Command {

    public static final Codec<CancelCommand> CODEC = Codecs.JSON_ELEMENT.fieldOf("value").xmap(CancelCommand::new, CancelCommand::element).codec();

    private final JsonElement element;
    private Object value;//This is not great

    @Override
    public boolean execute(EventContext context) {
        context.setReturnValue(value);
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.CANCEL;
    }

    @Override
    public DataResult<Void> validate(EventType type) {
        if (type.context().get(EventType.CANCEL_TERM).isEmpty())
            return DataResult.error(() -> "Event '%s' does not support cancellation");

        var val = type.context().get(EventType.CANCEL_TERM).orElseThrow().parse(JsonOps.INSTANCE, element);
        if (val.error().isPresent()) return val.map(object -> null);
        this.value = val.result().orElseThrow();

        return Command.super.validate(type);
    }
}
