package me.melontini.commander.event.builtin;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@UtilityClass
public class BuiltInEvents {
    public static void init() {
        ServerTick.init();
        EntityEvents.init();
        PlayerEvents.init();
    }

    public static LootContextParameterSet.Builder builder(Entity entity, ServerWorld world, Vec3d pos) {
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world);
        builder.add(LootContextParameters.THIS_ENTITY, entity);
        builder.add(LootContextParameters.ORIGIN, pos);
        return builder;
    }
}
