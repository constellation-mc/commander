package me.melontini.commander.impl.builtin;

import me.melontini.commander.api.expression.MacroBuilder;
import me.melontini.commander.api.expression.MacroRegistry;

import static net.minecraft.loot.context.LootContextParameters.*;

public class BuiltInMacros {

    public static void init() {
        MacroRegistry.register(ORIGIN, builder -> builder
                .arithmetic("x", context -> context.requireParameter(ORIGIN).x)
                .arithmetic("y", context -> context.requireParameter(ORIGIN).y)
                .arithmetic("z", context -> context.requireParameter(ORIGIN).z)
                .string("world/key", context -> context.getWorld().getRegistryKey().getValue().toString())
                .arithmetic("world/time", context -> context.getWorld().getTime())
                .arithmetic("world/day_time", context -> context.getWorld().getTimeOfDay())
                .arithmetic("world/seed", context -> context.getWorld().getSeed()));

        MacroRegistry.register(THIS_ENTITY, MacroBuilder.forEntity(context -> context.requireParameter(THIS_ENTITY)));
        MacroRegistry.register(KILLER_ENTITY, MacroBuilder.forEntity(context -> context.requireParameter(KILLER_ENTITY)));
        MacroRegistry.register(DIRECT_KILLER_ENTITY, MacroBuilder.forEntity(context -> context.requireParameter(DIRECT_KILLER_ENTITY)));
        MacroRegistry.register(LAST_DAMAGE_PLAYER, MacroBuilder.forEntity(context -> context.requireParameter(LAST_DAMAGE_PLAYER)));

        MacroRegistry.register(DAMAGE_SOURCE, builder -> builder
                .merge("source", MacroBuilder.forEntity(context -> context.requireParameter(DAMAGE_SOURCE).getSource()))
                .merge("attacker", MacroBuilder.forEntity(context -> context.requireParameter(DAMAGE_SOURCE).getAttacker())));

        MacroRegistry.register(BLOCK_STATE, MacroBuilder.empty());
        MacroRegistry.register(BLOCK_ENTITY, builder -> builder
                .arithmetic("x", context -> context.requireParameter(BLOCK_ENTITY).getPos().getX())
                .arithmetic("y", context -> context.requireParameter(BLOCK_ENTITY).getPos().getY())
                .arithmetic("z", context -> context.requireParameter(BLOCK_ENTITY).getPos().getZ()));

        MacroRegistry.register(TOOL, builder -> builder
                .arithmetic("count", context -> context.requireParameter(TOOL).getCount())
                .arithmetic("max_count", context -> context.requireParameter(TOOL).getMaxCount())
                .arithmetic("damage", context -> context.requireParameter(TOOL).getDamage())
                .arithmetic("max_damage", context -> context.requireParameter(TOOL).getMaxDamage()));

        MacroRegistry.register(EXPLOSION_RADIUS, builder -> builder.arithmetic("radius", context -> context.requireParameter(EXPLOSION_RADIUS)));
    }
}
