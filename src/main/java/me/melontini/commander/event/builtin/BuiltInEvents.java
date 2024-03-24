package me.melontini.commander.event.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventType;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class BuiltInEvents {

    public static final EventType NULL = EventTypes.register(id("none"), EventType.builder().build());

    public static void init() {
        ServerTick.init();
        EntityEvents.init();
        PlayerEvents.init();
    }
}
