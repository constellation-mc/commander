package me.melontini.commander.event.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

@UtilityClass
public class BuiltInEvents {
    public static void init() {
        ServerTick.init();
        EntityEvents.init();
        PlayerEvents.init();
    }

    public static void runVoid(EventType type, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return;

        EventContext context = new EventContext(supplier.get(), type);
        for (ConditionedCommand subscriber : subscribers) subscriber.execute(context);
    }

    public static boolean runBoolean(EventType type, boolean def, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return def;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return def;

        EventContext context = new EventContext(supplier.get(), type);
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(context);
            if (context.getReturnValue(null, def) != def) return def;
        }
        return def;
    }

    public static boolean runBoolean(EventType type, World world, Supplier<LootContext> supplier) {
        return runBoolean(type, true, world, supplier);
    }

    public static ActionResult runActionResult(EventType type, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return ActionResult.PASS;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return ActionResult.PASS;

        var context = new EventContext(supplier.get(), type);
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(context);
            ActionResult r = context.getReturnValue(null, null);
            if (r != null && r != ActionResult.PASS) return r;
        }
        return ActionResult.PASS;
    }
}
