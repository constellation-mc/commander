package me.melontini.commander.impl.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.impl.builtin.brigadier.ArithmeticaCommand;
import me.melontini.commander.impl.builtin.brigadier.DataCommand;
import me.melontini.commander.impl.builtin.brigadier.ExplodeCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

@UtilityClass
public class BrigadierCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ExplodeCommand.register(dispatcher);
            ArithmeticaCommand.register(dispatcher);
            DataCommand.register(dispatcher);
        });
    }
}
