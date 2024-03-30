package me.melontini.commander.util;

import lombok.experimental.UtilityClass;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventKey;
import me.melontini.commander.event.EventType;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

@UtilityClass
public class EventExecutors {
    public static void runVoid(EventType type, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return;

        EventContext context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (ConditionedCommand subscriber : subscribers) subscriber.execute(context);
    }

    public static boolean runBoolean(EventType type, boolean def, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return def;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return def;

        EventContext context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(context);
            boolean val = context.getReturnValue(null, def);
            if (val != def) return val;
        }
        return def;
    }

    public static boolean runBoolean(EventType type, World world, Supplier<LootContext> supplier) {
        return runBoolean(type, true, world, supplier);
    }

    public static <T extends Enum<T>> T runEnum(EventType type, T def, World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return def;

        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return def;

        var context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(context);
            T r = context.getReturnValue(null, def);
            if (r != def) return r;
        }
        return def;
    }

    public static ActionResult runActionResult(EventType type, World world, Supplier<LootContext> supplier) {
        return runEnum(type, ActionResult.PASS, world, supplier);
    }
}
