package me.melontini.commander.event.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class ServerTick {

    public static final EventType START_TICK = EventTypes.register(id("server_tick/start"), EventType.builder().build());
    public static final EventType END_TICK = EventTypes.register(id("server_tick/end"), EventType.builder().build());
    public static final EventType START_WORLD_TICK = EventTypes.register(id("world_tick/start"), EventType.builder().build());
    public static final EventType END_WORLD_TICK = EventTypes.register(id("world_tick/end"), EventType.builder().build());

    static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick(server.getOverworld(), START_TICK));
        ServerTickEvents.END_SERVER_TICK.register(server -> tick(server.getOverworld(), END_TICK));

        ServerTickEvents.START_WORLD_TICK.register((world) -> tick(world, START_WORLD_TICK));
        ServerTickEvents.END_WORLD_TICK.register((world) -> tick(world, END_WORLD_TICK));
    }

    private static void tick(ServerWorld world, EventType type) {
        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return;

        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world);
        builder.add(LootContextParameters.ORIGIN, Vec3d.ZERO);
        LootContext context = new LootContext.Builder(builder.build(LootContextTypes.COMMAND)).build(null);
        EventContext eventContext = new EventContext(context, type);

        for (ConditionedCommand subscriber : subscribers) subscriber.execute(eventContext);
    }
}
