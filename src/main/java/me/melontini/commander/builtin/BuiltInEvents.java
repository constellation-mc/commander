package me.melontini.commander.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.builtin.events.EntityEvents;
import me.melontini.commander.builtin.events.PlayerEvents;
import me.melontini.commander.builtin.events.ServerLifecycle;
import me.melontini.commander.builtin.events.ServerTick;

@UtilityClass
public class BuiltInEvents {

    public static void init() {
        ServerLifecycle.init();
        ServerTick.init();
        EntityEvents.init();
        PlayerEvents.init();
    }
}
