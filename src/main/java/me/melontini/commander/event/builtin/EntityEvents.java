package me.melontini.commander.event.builtin;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventType;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import static me.melontini.commander.Commander.id;
import static me.melontini.commander.util.EventExecutors.runBoolean;
import static me.melontini.commander.util.EventExecutors.runVoid;
import static net.minecraft.loot.context.LootContextParameters.*;

@UtilityClass
public class EntityEvents {

    public static final EventType ALLOW_DAMAGE = EventTypes.register(id("allow_damage"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType ALLOW_DEATH = EventTypes.register(id("allow_death"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType AFTER_DEATH = EventTypes.register(id("after_death"), EventType.builder().build());

    //public static final EventType ALLOW_SLEEPING = EventTypes.register(id("allow_sleeping"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType START_SLEEPING = EventTypes.register(id("start_sleeping"), EventType.builder().build());
    public static final EventType STOP_SLEEPING = EventTypes.register(id("stop_sleeping"), EventType.builder().build());

    public static final EventType ALLOW_ELYTRA = EventTypes.register(id("elytra_flight/allow"), EventType.builder().cancelTerm(Codec.BOOL).build());
    //public static final EventType CUSTOM_ELYTRA = EventTypes.register(id("elytra_flight/custom"), EventType.builder().cancelTerm(Codec.BOOL).build());

    public static final EventType ON_KILLED_BY = EventTypes.register(id("after_killed_by_other"), EventType.builder().build());

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> runBoolean(ALLOW_DAMAGE, entity.getWorld(), () -> makeContext(entity, source.getPosition(), source)));
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> runBoolean(ALLOW_DEATH, entity.getWorld(), () -> makeContext(entity, source.getPosition(), source)));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> runVoid(AFTER_DEATH, entity.getWorld(), () -> makeContext(entity, source.getPosition(), source)));

        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> runVoid(START_SLEEPING, entity.getWorld(), () -> makeContext(entity, Vec3d.ofCenter(sleepingPos), null)));
        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> runVoid(STOP_SLEEPING, entity.getWorld(), () -> makeContext(entity, Vec3d.ofCenter(sleepingPos), null)));

        EntityElytraEvents.ALLOW.register(entity -> runBoolean(ALLOW_ELYTRA, entity.getWorld(), () -> makeContext(entity, entity.getPos(), null)));

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> runVoid(ON_KILLED_BY, world, () -> {
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) entity.getWorld());
            builder.add(THIS_ENTITY, killedEntity).add(ORIGIN, killedEntity.getPos());
            builder.add(DAMAGE_SOURCE, world.getDamageSources().generic()).add(KILLER_ENTITY, entity);
            return new LootContext.Builder(builder.build(LootContextTypes.ENTITY)).build(null);
        }));
    }

    private static LootContext makeContext(Entity entity, Vec3d origin, DamageSource source) {
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) entity.getWorld());
        builder.add(THIS_ENTITY, entity).add(ORIGIN, origin);
        if (source != null) {
            builder.add(DAMAGE_SOURCE, source);
            builder.addOptional(DIRECT_KILLER_ENTITY, source.getAttacker());
            builder.addOptional(KILLER_ENTITY, source.getSource());
        }
        return new LootContext.Builder(builder.build(source == null ? LootContextTypes.COMMAND : LootContextTypes.ENTITY)).build(null);
    }
}
