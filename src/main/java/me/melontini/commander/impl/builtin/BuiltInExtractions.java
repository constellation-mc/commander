package me.melontini.commander.impl.builtin;

import me.melontini.commander.api.expression.ExtractionBuilder;
import me.melontini.commander.api.expression.ExtractionRegistry;

import static net.minecraft.loot.context.LootContextParameters.*;

public class BuiltInExtractions {

    public static void init() {
        ExtractionRegistry.register(ORIGIN, builder -> builder
                .arithmetic("x", context -> context.requireParameter(ORIGIN).x)
                .arithmetic("y", context -> context.requireParameter(ORIGIN).y)
                .arithmetic("z", context -> context.requireParameter(ORIGIN).z)
                .string("world/key", context -> context.getWorld().getRegistryKey().getValue().toString())
                .arithmetic("world/time", context -> context.getWorld().getTime())
                .arithmetic("world/day_time", context -> context.getWorld().getTimeOfDay())
                .arithmetic("world/seed", context -> context.getWorld().getSeed()));

        ExtractionRegistry.register(THIS_ENTITY, ExtractionBuilder.forEntity(context -> context.get(THIS_ENTITY)));
        ExtractionRegistry.register(KILLER_ENTITY, ExtractionBuilder.forEntity(context -> context.get(KILLER_ENTITY)));
        ExtractionRegistry.register(DIRECT_KILLER_ENTITY, ExtractionBuilder.forEntity(context -> context.get(DIRECT_KILLER_ENTITY)));
        ExtractionRegistry.register(LAST_DAMAGE_PLAYER, ExtractionBuilder.forEntity(context -> context.get(LAST_DAMAGE_PLAYER)));

        ExtractionRegistry.register(DAMAGE_SOURCE, builder -> builder
                .merge("source", ExtractionBuilder.forEntity(context -> context.requireParameter(DAMAGE_SOURCE).getSource()))
                .merge("attacker", ExtractionBuilder.forEntity(context -> context.requireParameter(DAMAGE_SOURCE).getAttacker())));

        ExtractionRegistry.register(BLOCK_STATE, ExtractionBuilder.empty());
        ExtractionRegistry.register(BLOCK_ENTITY, builder -> builder
                .arithmetic("x", context -> context.requireParameter(BLOCK_ENTITY).getPos().getX())
                .arithmetic("y", context -> context.requireParameter(BLOCK_ENTITY).getPos().getY())
                .arithmetic("z", context -> context.requireParameter(BLOCK_ENTITY).getPos().getZ()));

        ExtractionRegistry.register(TOOL, builder -> builder
                .arithmetic("count", context -> context.requireParameter(TOOL).getCount())
                .arithmetic("max_count", context -> context.requireParameter(TOOL).getMaxCount())
                .arithmetic("damage", context -> context.requireParameter(TOOL).getDamage())
                .arithmetic("max_damage", context -> context.requireParameter(TOOL).getMaxDamage()));

        ExtractionRegistry.register(EXPLOSION_RADIUS, builder -> builder.arithmetic("radius", context -> context.requireParameter(EXPLOSION_RADIUS)));
    }
}
