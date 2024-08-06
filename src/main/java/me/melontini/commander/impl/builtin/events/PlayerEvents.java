package me.melontini.commander.impl.builtin.events;

import static me.melontini.commander.impl.Commander.id;
import static net.minecraft.loot.context.LootContextParameters.*;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.util.EventExecutors;
import me.melontini.commander.impl.util.loot.LootUtil;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class PlayerEvents {

  public static final EventType ATTACK_BLOCK = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("player_attack/block"));
  public static final EventType USE_BLOCK = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("player_use/block"));

  public static final EventType ATTACK_ENTITY = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("player_attack/entity"));
  public static final EventType USE_ENTITY = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("player_use/entity"));

  public static final EventType USE_ITEM = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(id("player_use/item"));

  public static final EventType BEFORE_BREAK =
      EventType.builder().cancelTerm(Codec.BOOL).build(id("player_break_block/before"));
  public static final EventType AFTER_BREAK =
      EventType.builder().build(id("player_break_block/after"));
  public static final EventType CANCELLED_BREAK =
      EventType.builder().build(id("player_break_block/cancelled"));

  public static void init() {
    AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> blockCallback(
        ATTACK_BLOCK, world, player, player.getStackInHand(hand), Vec3d.ofCenter(pos), pos));
    UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> blockCallback(
        USE_BLOCK,
        world,
        player,
        player.getStackInHand(hand),
        hitResult.getPos(),
        hitResult.getBlockPos()));

    AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
        entityCallback(ATTACK_ENTITY, world, player, hand, entity));
    UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
        entityCallback(USE_ENTITY, world, player, hand, entity));

    UseItemCallback.EVENT.register((player, world, hand) -> new TypedActionResult<>(
        EventExecutors.runActionResult(
            USE_ITEM,
            world,
            () -> LootUtil.build(new LootContextParameterSet.Builder((ServerWorld) world)
                .add(THIS_ENTITY, player)
                .add(ORIGIN, player.getPos())
                .add(TOOL, player.getStackInHand(hand))
                .build(LootContextTypes.FISHING))),
        player.getStackInHand(hand)));

    PlayerBlockBreakEvents.BEFORE.register(
        (world, player, pos, state, blockEntity) -> EventExecutors.runBoolean(
            BEFORE_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
    PlayerBlockBreakEvents.AFTER.register(
        (world, player, pos, state, blockEntity) -> EventExecutors.runVoid(
            AFTER_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
    PlayerBlockBreakEvents.CANCELED.register(
        (world, player, pos, state, blockEntity) -> EventExecutors.runVoid(
            CANCELLED_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
  }

  private static LootContext breakContext(
      World world,
      PlayerEntity player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {
    return LootUtil.build(new LootContextParameterSet.Builder((ServerWorld) world)
        .add(THIS_ENTITY, player)
        .add(ORIGIN, Vec3d.ofCenter(pos))
        .add(BLOCK_STATE, state)
        .add(TOOL, ItemStack.EMPTY)
        .addOptional(BLOCK_ENTITY, blockEntity)
        .build(LootContextTypes.BLOCK));
  }

  private static ActionResult entityCallback(
      EventType type, World world, PlayerEntity player, Hand hand, Entity entity) {
    if (world.isClient()) return ActionResult.PASS;
    return EventExecutors.runActionResult(type, world, () -> {
      ItemStack tool = player.getStackInHand(hand);
      DamageSource source = world.getDamageSources().generic();

      return LootUtil.build(new LootContextParameterSet.Builder((ServerWorld) world)
          .add(THIS_ENTITY, entity)
          .add(ORIGIN, entity.getPos())
          .add(TOOL, tool)
          .add(LAST_DAMAGE_PLAYER, player)
          .add(DAMAGE_SOURCE, source)
          .build(LootContextTypes.ENTITY));
    });
  }

  private static ActionResult blockCallback(
      EventType type,
      World world,
      PlayerEntity player,
      ItemStack tool,
      Vec3d origin,
      BlockPos pos) {
    if (world.isClient()) return ActionResult.PASS;
    return EventExecutors.runActionResult(type, world, () -> {
      BlockState state = world.getBlockState(pos);
      BlockEntity blockEntity = world.getBlockEntity(pos);

      return LootUtil.build(new LootContextParameterSet.Builder((ServerWorld) world)
          .add(THIS_ENTITY, player)
          .add(ORIGIN, origin)
          .add(BLOCK_STATE, state)
          .add(TOOL, tool)
          .addOptional(BLOCK_ENTITY, blockEntity)
          .build(LootContextTypes.BLOCK));
    });
  }
}
