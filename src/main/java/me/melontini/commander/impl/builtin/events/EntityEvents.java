package me.melontini.commander.impl.builtin.events;

import static me.melontini.commander.api.util.EventExecutors.*;
import static me.melontini.commander.impl.Commander.id;
import static net.minecraft.loot.context.LootContextParameters.*;

import com.mojang.serialization.Codec;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.impl.util.loot.LootUtil;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class EntityEvents {

  public static final EventType ALLOW_DAMAGE =
      EventType.builder().cancelTerm(Codec.BOOL).build(id("allow_damage"));
  public static final EventType ALLOW_DEATH =
      EventType.builder().cancelTerm(Codec.BOOL).build(id("allow_death"));
  public static final EventType AFTER_DEATH = EventType.builder().build(id("after_death"));

  public static final EventType START_SLEEPING = EventType.builder().build(id("sleeping/start"));
  public static final EventType STOP_SLEEPING = EventType.builder().build(id("sleeping/stop"));
  public static final EventType ALLOW_SLEEPING = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(PlayerEntity.SleepFailureReason.class))
      .build(id("sleeping/allow"));
  public static final EventType ALLOW_SLEEP_TIME = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("sleeping/allow_time"));
  public static final EventType ALLOW_NEARBY_MONSTERS = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("sleeping/allow_nearby_monsters"));

  public static final EventType ALLOW_ELYTRA =
      EventType.builder().cancelTerm(Codec.BOOL).build(id("elytra_flight/allow"));
  // public static final EventType CUSTOM_ELYTRA =
  // EventType.builder().cancelTerm(Codec.BOOL).build(id("elytra_flight/custom"));

  public static final EventType AFTER_KILLED_BY_OTHER =
      EventType.builder().build(id("after_killed_by_other"));

  public static void init() {
    ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> runBoolean(
        ALLOW_DAMAGE,
        entity.getWorld(),
        () -> makeContext(
            entity, Objects.requireNonNullElse(source.getPosition(), entity.getPos()), source)));
    ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> runBoolean(
        ALLOW_DEATH,
        entity.getWorld(),
        () -> makeContext(
            entity, Objects.requireNonNullElse(source.getPosition(), entity.getPos()), source)));

    ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> runVoid(
        AFTER_DEATH,
        entity.getWorld(),
        () -> makeContext(
            entity, Objects.requireNonNullElse(source.getPosition(), entity.getPos()), source)));

    EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> runVoid(
        START_SLEEPING,
        entity.getWorld(),
        () -> makeContext(entity, Vec3d.ofCenter(sleepingPos), null)));
    EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> runVoid(
        STOP_SLEEPING,
        entity.getWorld(),
        () -> makeContext(entity, Vec3d.ofCenter(sleepingPos), null)));
    EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> runEnum(
        ALLOW_SLEEPING,
        null,
        player.getWorld(),
        () -> makeContext(player, Vec3d.ofCenter(sleepingPos), null)));
    EntitySleepEvents.ALLOW_SLEEP_TIME.register(
        (player, sleepingPos, vanillaResult) -> runActionResult(
            ALLOW_SLEEP_TIME,
            player.getWorld(),
            () -> makeContext(player, Vec3d.ofCenter(sleepingPos), null)));
    EntitySleepEvents.ALLOW_NEARBY_MONSTERS.register(
        (player, sleepingPos, vanillaResult) -> runActionResult(
            ALLOW_NEARBY_MONSTERS,
            player.getWorld(),
            () -> makeContext(player, Vec3d.ofCenter(sleepingPos), null)));

    EntityElytraEvents.ALLOW.register(entity -> runBoolean(
        ALLOW_ELYTRA, entity.getWorld(), () -> makeContext(entity, entity.getPos(), null)));

    ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(
        (world, entity, killedEntity) -> runVoid(
            AFTER_KILLED_BY_OTHER,
            world,
            () ->
                LootUtil.build(new LootContextParameterSet.Builder((ServerWorld) entity.getWorld())
                    .add(THIS_ENTITY, killedEntity)
                    .add(ORIGIN, killedEntity.getPos())
                    .add(DAMAGE_SOURCE, world.getDamageSources().generic())
                    .add(ATTACKING_ENTITY, entity)
                    .build(LootContextTypes.ENTITY))));
  }

  private static LootContext makeContext(
      Entity entity, Vec3d origin, @Nullable DamageSource source) {
    LootContextParameterSet.Builder builder =
        new LootContextParameterSet.Builder((ServerWorld) entity.getWorld());
    builder.add(THIS_ENTITY, entity).add(ORIGIN, origin);
    if (source != null) {
      builder
          .add(DAMAGE_SOURCE, source)
          .addOptional(DIRECT_ATTACKING_ENTITY, source.getAttacker())
          .addOptional(ATTACKING_ENTITY, source.getSource());
    }
    return LootUtil.build(
        builder.build(source == null ? LootContextTypes.COMMAND : LootContextTypes.ENTITY));
  }
}
