package me.melontini.commander.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.builtin.brigadier.ExplodeCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

@UtilityClass
public class BrigadierCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ExplodeCommand.register(dispatcher);
        });
    }
}
