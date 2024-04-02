package me.melontini.commander.builtin.commands.action;

import com.mojang.serialization.Codec;
import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.util.math.Arithmetica;

public record PrintArithmetica(Arithmetica arithmetica) implements Command {

    public static final Codec<PrintArithmetica> CODEC = Arithmetica.CODEC.xmap(PrintArithmetica::new, PrintArithmetica::arithmetica).fieldOf("arithmetica").codec();

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
