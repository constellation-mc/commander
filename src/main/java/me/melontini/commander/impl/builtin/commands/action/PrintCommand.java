package me.melontini.commander.impl.builtin.commands.action;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.builtin.BuiltInCommands;

public record PrintCommand(String text) implements Command {

    public static final Codec<PrintCommand> CODEC = Codec.STRING.fieldOf("text").xmap(PrintCommand::new, PrintCommand::text).codec();

    @Override
    public boolean execute(EventContext context) {
        System.out.println(text());
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.PRINT;
    }
}
