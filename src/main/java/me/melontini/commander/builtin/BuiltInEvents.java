package me.melontini.commander.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.builtin.events.EntityEvents;
import me.melontini.commander.builtin.events.PlayerEvents;
import me.melontini.commander.builtin.events.ServerTick;
import me.melontini.commander.event.EventType;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class BuiltInEvents {

    public static final EventType NULL = EventType.builder()
            .extension(null, subscriptions -> null).build(id("none"));

    public static void init() {
        ServerTick.init();
        EntityEvents.init();
        PlayerEvents.init();
    }
}
