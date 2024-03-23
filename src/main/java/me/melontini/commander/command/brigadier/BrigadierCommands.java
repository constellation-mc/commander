package me.melontini.commander.command.brigadier;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class BrigadierCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ExplodeCommand.register(dispatcher);
        });
    }
}
