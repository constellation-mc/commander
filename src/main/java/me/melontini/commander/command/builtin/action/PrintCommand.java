package me.melontini.commander.command.builtin.action;

import com.mojang.serialization.Codec;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.builtin.BuiltInCommands;
import me.melontini.commander.event.EventContext;

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
