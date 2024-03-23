package me.melontini.commander.event.builtin;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class EntityEvents {

    public static final EventType ALLOW_DAMAGE = EventTypes.register(id("allow_damage"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType ALLOW_DEATH = EventTypes.register(id("allow_death"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType AFTER_DEATH = EventTypes.register(id("after_death"), EventType.builder().build());

    //public static final EventType ALLOW_SLEEPING = EventTypes.register(id("allow_sleeping"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType START_SLEEPING = EventTypes.register(id("start_sleeping"), EventType.builder().build());
    public static final EventType STOP_SLEEPING = EventTypes.register(id("stop_sleeping"), EventType.builder().build());

    public static final EventType ALLOW_ELYTRA = EventTypes.register(id("allow_elytra_flight"), EventType.builder().cancelTerm(Codec.BOOL).build());

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> executeReturn(ALLOW_DAMAGE, entity, source.getPosition(), source));
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> executeReturn(ALLOW_DEATH, entity, source.getPosition(), source));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> execute(AFTER_DEATH, entity, source.getPosition(), source));

        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> execute(START_SLEEPING, entity, Vec3d.ofCenter(sleepingPos), null));
        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> execute(STOP_SLEEPING, entity, Vec3d.ofCenter(sleepingPos), null));

        EntityElytraEvents.ALLOW.register(entity -> executeReturn(ALLOW_ELYTRA, entity, entity.getPos(), null));
    }

    private static void execute(EventType type, Entity entity, Vec3d origin, DamageSource source) {
        if (entity.getWorld().isClient()) return;
        var subscribers = DynamicEventManager.getData(MakeSure.notNull(entity.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return;

        var eventContext = makeContext(type, entity, origin, source);
        for (ConditionedCommand subscriber : subscribers) subscriber.execute(eventContext);
    }

    private static boolean executeReturn(EventType type, Entity entity, Vec3d origin, DamageSource source) {
        if (entity.getWorld().isClient()) return true;
        var subscribers = DynamicEventManager.getData(MakeSure.notNull(entity.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return true;

        var eventContext = makeContext(type, entity, origin, source);
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(eventContext);
            if (!eventContext.getReturnValue(null, true)) return false;
        }
        return true;
    }

    private static EventContext makeContext(EventType type, Entity entity, Vec3d origin, DamageSource source) {
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) entity.getWorld());
        builder.add(LootContextParameters.THIS_ENTITY, entity);
        builder.add(LootContextParameters.ORIGIN, origin);
        if (source != null) {
            builder.add(LootContextParameters.DAMAGE_SOURCE, source);
            builder.addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, source.getAttacker());
            builder.addOptional(LootContextParameters.KILLER_ENTITY, source.getSource());
        }
        LootContext context = new LootContext.Builder(builder.build(source == null ? LootContextTypes.COMMAND : LootContextTypes.ENTITY)).build(null);
        return new EventContext(context, type);
    }
}
