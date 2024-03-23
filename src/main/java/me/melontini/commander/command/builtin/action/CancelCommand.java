package me.melontini.commander.command.builtin.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.builtin.BuiltInCommands;
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
        var term = context.type().context().get(EventType.CANCEL_TERM);
        if (term.isEmpty()) throw new IllegalStateException("Event does not support cancellation");

        if (value == null) {
            value = term.get().parse(JsonOps.INSTANCE, element).getOrThrow(false, string -> {
                throw new JsonParseException(string);
            });
        }

        context.setReturnValue(value);
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.CANCEL;
    }
}
