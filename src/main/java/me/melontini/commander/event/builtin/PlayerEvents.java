package me.melontini.commander.event.builtin;

import lombok.experimental.UtilityClass;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.event.EventType;
import me.melontini.commander.util.MagicCodecs;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

import static me.melontini.commander.Commander.id;

@UtilityClass
public class PlayerEvents {

    public static final EventType ATTACK_BLOCK = EventTypes.register(id("player_attack/block"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());
    public static final EventType USE_BLOCK = EventTypes.register(id("player_use/block"), EventType.builder().cancelTerm(MagicCodecs.enumCodec(ActionResult.class)).build());

    static void init() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> blockCallback(ATTACK_BLOCK, world, player, hand, Vec3d.ofCenter(pos), pos));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> blockCallback(USE_BLOCK, world, player, hand, hitResult.getPos(), hitResult.getBlockPos()));
    }

    private static ActionResult blockCallback(EventType type, World world, PlayerEntity player, Hand hand, Vec3d origin, BlockPos pos) {
        if (world.isClient()) return ActionResult.PASS;
        return executeReturn(type, world, () -> {
            ItemStack tool = player.getStackInHand(hand);
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            LootContextParameterSet.Builder builder = BuiltInEvents.builder(player, (ServerWorld) world, origin);
            builder.add(LootContextParameters.BLOCK_STATE, state);
            builder.add(LootContextParameters.TOOL, tool);
            builder.addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity);
            return new LootContext.Builder(builder.build(LootContextTypes.BLOCK)).build(null);
        });
    }

    private static ActionResult executeReturn(EventType type, World world, Supplier<LootContext> context) {
        var subscribers = DynamicEventManager.getData(MakeSure.notNull(world.getServer()), type, DynamicEventManager.DEFAULT);
        if (subscribers.isEmpty()) return ActionResult.PASS;

        var eventContext = new EventContext(context.get(), type);
        for (ConditionedCommand subscriber : subscribers) {
            subscriber.execute(eventContext);
            ActionResult r = eventContext.getReturnValue(null, null);
            if (r != null && r != ActionResult.PASS) return r;
        }
        return ActionResult.PASS;
    }
}
