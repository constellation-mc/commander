package me.melontini.commander.impl.builtin.events;

import static me.melontini.commander.api.util.EventExecutors.runVoid;
import static me.melontini.commander.impl.Commander.id;

import lombok.experimental.UtilityClass;
import me.melontini.commander.api.event.EventType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

import static me.melontini.commander.api.util.EventExecutors.runVoid;
import static me.melontini.commander.impl.Commander.id;

@UtilityClass
public class ServerTick {

  public static final EventType START_TICK = EventType.builder().build(id("server_tick/start"));
  public static final EventType END_TICK = EventType.builder().build(id("server_tick/end"));
  public static final EventType START_WORLD_TICK =
      EventType.builder().build(id("world_tick/start"));
  public static final EventType END_WORLD_TICK = EventType.builder().build(id("world_tick/end"));

  public static void init() {
    ServerTickEvents.START_SERVER_TICK.register(server ->
        runVoid(START_TICK, server.getOverworld(), () -> forWorld(server.getOverworld())));
    ServerTickEvents.END_SERVER_TICK.register(
        server -> runVoid(END_TICK, server.getOverworld(), () -> forWorld(server.getOverworld())));

    ServerTickEvents.START_WORLD_TICK.register(
        (world) -> runVoid(START_WORLD_TICK, world, () -> forWorld(world)));
    ServerTickEvents.END_WORLD_TICK.register(
        (world) -> runVoid(END_WORLD_TICK, world, () -> forWorld(world)));
  }

  private static LootContext forWorld(ServerWorld world) {
    LootContextParameterSet.Builder builder =
        new LootContextParameterSet.Builder(world).add(LootContextParameters.ORIGIN, Vec3d.ZERO);
    return new LootContext.Builder(builder.build(LootContextTypes.COMMAND)).build(Optional.empty());
  }
}
