package dev.zenfyr.commander.impl.brigadier;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

@UtilityClass
public class BrigadierCommands {

  public static void init() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      ExpressionCommand.register(dispatcher);
      DataCommand.register(dispatcher);
    });
  }
}
