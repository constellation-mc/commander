package me.melontini.commander.impl.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

@UtilityClass
public class ServerHelper {

    public static void broadcastToOps(MinecraftServer server, Text text) {
        server.getPlayerManager().broadcast(text, player -> {
            if (server.getPlayerManager().isOperator(player.getGameProfile())) {
                return text;
            }
            return null;
        }, false);
    }
}
