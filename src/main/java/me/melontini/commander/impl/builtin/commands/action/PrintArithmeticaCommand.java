package me.melontini.commander.impl.builtin.commands.action;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.impl.builtin.BuiltInCommands;

public record PrintArithmeticaCommand(Arithmetica arithmetica) implements Command {

    public static final Codec<PrintArithmeticaCommand> CODEC = Arithmetica.CODEC.xmap(PrintArithmeticaCommand::new, PrintArithmeticaCommand::arithmetica).fieldOf("value").codec();

    @Override
    public boolean execute(EventContext context) {
        System.out.println(arithmetica().apply(context.lootContext()));
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.PRINT_ARITHMETICA;
    }
}
