package me.melontini.commander.event.builtin;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventType;
import me.melontini.commander.util.MagicCodecs;
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

import static me.melontini.commander.Commander.id;
import static me.melontini.commander.event.builtin.BuiltInEvents.*;
import static net.minecraft.loot.context.LootContextParameters.*;

@UtilityClass
public class PlayerEvents {

    public static final EventType ATTACK_BLOCK = EventTypes.register(id("player_attack/block"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());
    public static final EventType USE_BLOCK = EventTypes.register(id("player_use/block"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());

    public static final EventType ATTACK_ENTITY = EventTypes.register(id("player_attack/entity"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());
    public static final EventType USE_ENTITY = EventTypes.register(id("player_use/entity"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());

    public static final EventType USE_ITEM = EventTypes.register(id("player_use/item"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());

    public static final EventType BEFORE_BREAK = EventTypes.register(id("player_break_block/before"), EventType.builder().cancelTerm(Codec.BOOL).build());
    public static final EventType AFTER_BREAK = EventTypes.register(id("player_break_block/after"), EventType.builder().build());
    public static final EventType CANCELLED_BREAK = EventTypes.register(id("player_break_block/cancelled"), EventType.builder().build());

    static void init() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> blockCallback(ATTACK_BLOCK, world, player, player.getStackInHand(hand), Vec3d.ofCenter(pos), pos));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> blockCallback(USE_BLOCK, world, player, player.getStackInHand(hand), hitResult.getPos(), hitResult.getBlockPos()));

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> entityCallback(ATTACK_ENTITY, world, player, hand, entity));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> entityCallback(USE_ENTITY, world, player, hand, entity));

        UseItemCallback.EVENT.register((player, world, hand) -> new TypedActionResult<>(runActionResult(USE_ITEM, world, () -> {
            LootContextParameterSet.Builder builder =  new LootContextParameterSet.Builder((ServerWorld) world)
                    .add(THIS_ENTITY, player)
                    .add(ORIGIN, player.getPos())
                    .add(TOOL, player.getStackInHand(hand));
            return new LootContext.Builder(builder.build(LootContextTypes.FISHING)).build(null);
        }), player.getStackInHand(hand)));

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> runBoolean(BEFORE_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> runVoid(AFTER_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
        PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> runVoid(CANCELLED_BREAK, world, () -> breakContext(world, player, pos, state, blockEntity)));
    }

    private static LootContext breakContext(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) world)
                .add(THIS_ENTITY, player).add(ORIGIN, Vec3d.ofCenter(pos))
                .add(BLOCK_STATE, state).add(TOOL, ItemStack.EMPTY)
                .addOptional(BLOCK_ENTITY, blockEntity);
        return new LootContext.Builder(builder.build(LootContextTypes.BLOCK)).build(null);
    }

    private static ActionResult entityCallback(EventType type, World world, PlayerEntity player, Hand hand, Entity entity) {
        if (world.isClient()) return ActionResult.PASS;
        return runActionResult(type, world, () -> {
            ItemStack tool = player.getStackInHand(hand);
            DamageSource source = world.getDamageSources().generic();

            LootContextParameterSet.Builder builder =  new LootContextParameterSet.Builder((ServerWorld) world)
                    .add(THIS_ENTITY, entity).add(ORIGIN, entity.getPos())
                    .add(TOOL, tool).add(LAST_DAMAGE_PLAYER, player)
                    .add(DAMAGE_SOURCE, source);
            return new LootContext.Builder(builder.build(LootContextTypes.ENTITY)).build(null);
        });
    }

    private static ActionResult blockCallback(EventType type, World world, PlayerEntity player, ItemStack tool, Vec3d origin, BlockPos pos) {
        if (world.isClient()) return ActionResult.PASS;
        return runActionResult(type, world, () -> {
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) world)
                    .add(THIS_ENTITY, player).add(ORIGIN,origin)
                    .add(BLOCK_STATE, state).add(TOOL, tool)
                    .addOptional(BLOCK_ENTITY, blockEntity);
            return new LootContext.Builder(builder.build(LootContextTypes.BLOCK)).build(null);
        });
    }
}
