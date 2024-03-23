package me.melontini.commander.command.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.brigadier.BrigadierCommands;
import me.melontini.commander.command.builtin.action.CancelCommand;
import me.melontini.commander.command.builtin.action.CommandCommand;
import me.melontini.commander.command.builtin.action.PrintCommand;
import me.melontini.commander.command.builtin.logic.AllOfCommand;
import me.melontini.commander.command.builtin.logic.AnyOfCommand;
import me.melontini.commander.command.builtin.logic.DefaultedCommand;
import me.melontini.commander.command.builtin.logic.RandomCommand;
import me.melontini.commander.data.types.CommandTypes;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class BuiltInCommands {

    public static final CommandType RANDOM = CommandTypes.register(id("random"), RandomCommand.CODEC);
    public static final CommandType ALL_OF = CommandTypes.register(id("all_of"), AllOfCommand.CODEC);
    public static final CommandType ANY_OF = CommandTypes.register(id("any_of"), AnyOfCommand.CODEC);
    public static final CommandType DEFAULTED = CommandTypes.register(id("defaulted"), DefaultedCommand.CODEC);


    public static final CommandType CANCEL = CommandTypes.register(id("cancel"), CancelCommand.CODEC);
    public static final CommandType COMMANDS = CommandTypes.register(id("commands"), CommandCommand.CODEC);
    public static final CommandType PRINT = CommandTypes.register(id("print"), PrintCommand.CODEC);

    public static void init() {
        BrigadierCommands.init();
    }
}
