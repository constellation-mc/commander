package me.melontini.commander.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.builtin.commands.action.CancelCommand;
import me.melontini.commander.builtin.commands.action.CommandCommand;
import me.melontini.commander.builtin.commands.action.PrintArithmetica;
import me.melontini.commander.builtin.commands.action.PrintCommand;
import me.melontini.commander.builtin.commands.logic.AllOfCommand;
import me.melontini.commander.builtin.commands.logic.AnyOfCommand;
import me.melontini.commander.builtin.commands.logic.DefaultedCommand;
import me.melontini.commander.builtin.commands.logic.RandomCommand;
import me.melontini.commander.command.CommandType;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class BuiltInCommands {

    public static final CommandType RANDOM = CommandType.register(id("random"), RandomCommand.CODEC);
    public static final CommandType ALL_OF = CommandType.register(id("all_of"), AllOfCommand.CODEC);
    public static final CommandType ANY_OF = CommandType.register(id("any_of"), AnyOfCommand.CODEC);
    public static final CommandType DEFAULTED = CommandType.register(id("defaulted"), DefaultedCommand.CODEC);


    public static final CommandType CANCEL = CommandType.register(id("cancel"), CancelCommand.CODEC);
    public static final CommandType COMMANDS = CommandType.register(id("commands"), CommandCommand.CODEC);
    public static final CommandType PRINT = CommandType.register(id("print"), PrintCommand.CODEC);
    public static final CommandType PRINT_ARITHMETICA = CommandType.register(id("print_arithmetica"), PrintArithmetica.CODEC);

    public static void init() {
        BrigadierCommands.init();
    }
}
