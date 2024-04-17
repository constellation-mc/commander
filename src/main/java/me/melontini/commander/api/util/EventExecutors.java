package me.melontini.commander.api.util;

import lombok.experimental.UtilityClass;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventKey;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.event.Subscription;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class EventExecutors {
    public static void runVoid(EventType type, @NotNull World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return;

        List<Command.Conditioned> subscribers = Subscription.getData(MakeSure.notNull(world.getServer()), type);
        if (subscribers.isEmpty()) return;

        EventContext context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (Command.Conditioned subscriber : subscribers) subscriber.execute(context);
    }

    public static boolean runBoolean(EventType type, boolean def, @NotNull World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return def;

        List<Command.Conditioned> subscribers = Subscription.getData(MakeSure.notNull(world.getServer()), type);
        if (subscribers.isEmpty()) return def;

        EventContext context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (Command.Conditioned subscriber : subscribers) {
            subscriber.execute(context);
            boolean val = context.getReturnValue(def);
            if (val != def) return val;
        }
        return def;
    }

    public static boolean runBoolean(EventType type, World world, Supplier<LootContext> supplier) {
        return runBoolean(type, true, world, supplier);
    }

    public static <T extends Enum<T>> T runEnum(EventType type, T def, @NotNull World world, Supplier<LootContext> supplier) {
        if (world.isClient()) return def;

        List<Command.Conditioned> subscribers = Subscription.getData(MakeSure.notNull(world.getServer()), type);
        if (subscribers.isEmpty()) return def;

        var context = EventContext.builder(type)
                .addParameter(EventKey.LOOT_CONTEXT, supplier.get())
                .build();
        for (Command.Conditioned subscriber : subscribers) {
            subscriber.execute(context);
            T r = context.getReturnValue(def);
            if (r != def) return r;
        }
        return def;
    }

    public static ActionResult runActionResult(EventType type, World world, Supplier<LootContext> supplier) {
        return runEnum(type, ActionResult.PASS, world, supplier);
    }
}
