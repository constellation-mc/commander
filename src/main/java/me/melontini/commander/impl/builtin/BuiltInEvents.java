package me.melontini.commander.impl.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.impl.builtin.events.EntityEvents;
import me.melontini.commander.impl.builtin.events.PlayerEvents;
import me.melontini.commander.impl.builtin.events.ServerLifecycle;
import me.melontini.commander.impl.builtin.events.ServerTick;

@UtilityClass
public class BuiltInEvents {

  public static void init() {
    ServerLifecycle.init();
    ServerTick.init();
    EntityEvents.init();
    PlayerEvents.init();
  }
}
