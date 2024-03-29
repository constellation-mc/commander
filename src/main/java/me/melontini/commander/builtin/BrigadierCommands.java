package me.melontini.commander.builtin;

import me.melontini.commander.builtin.brigadier.ExplodeCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class BrigadierCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ExplodeCommand.register(dispatcher);
        });
    }
}
