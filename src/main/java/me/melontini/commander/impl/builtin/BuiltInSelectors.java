package me.melontini.commander.impl.builtin;

import static me.melontini.commander.impl.Commander.id;

import lombok.experimental.UtilityClass;
import me.melontini.commander.api.command.Selector;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@UtilityClass
@SuppressWarnings("unused")
public final class BuiltInSelectors {

  public static final Selector SERVER =
      Selector.register(mc("server"), context -> context.getWorld().getServer().getCommandSource());
  public static final Selector ORIGIN = Selector.register(mc("origin"), context -> {
    var world = context.getWorld();
    var o = context.requireParameter(LootContextParameters.ORIGIN);

    return new ServerCommandSource(
        world.getServer(),
        o,
        Vec2f.ZERO,
        world,
        4,
        world.getRegistryKey().getValue().toString(),
        TextUtil.literal(world.getRegistryKey().getValue().toString()),
        world.getServer(),
        null);
  });
  public static final Selector THIS_ENTITY = Selector.register(
      mc("this_entity"),
      context -> forEntity(context.requireParameter(LootContextParameters.THIS_ENTITY)));
  public static final Selector KILLER_ENTITY = Selector.register(
      mc("attacking_entity"),
      context -> forEntity(context.requireParameter(LootContextParameters.ATTACKING_ENTITY)));
  public static final Selector DIRECT_KILLER_ENTITY = Selector.register(
      mc("direct_attacking_entity"),
      context -> forEntity(context.requireParameter(LootContextParameters.DIRECT_ATTACKING_ENTITY)));
  public static final Selector LAST_DAMAGE_PLAYER = Selector.register(
      mc("last_damage_player"),
      context -> forEntity(context.requireParameter(LootContextParameters.LAST_DAMAGE_PLAYER)));
  public static final Selector BLOCK_ENTITY = Selector.register(mc("block_entity"), context -> {
    var be = context.requireParameter(LootContextParameters.BLOCK_ENTITY);
    return new ServerCommandSource(
        context.getWorld().getServer(),
        Vec3d.ofCenter(be.getPos()),
        Vec2f.ZERO,
        (ServerWorld) be.getWorld(),
        4,
        "BlockEntity",
        TextUtil.literal("BlockEntity"),
        context.getWorld().getServer(),
        null);
  });

  public static final Selector DAMAGE_SOURCE_SOURCE =
      Selector.register(id("damage_source/source"), context -> {
        var s = context.requireParameter(LootContextParameters.DAMAGE_SOURCE).getSource();
        return s != null ? forEntity(s) : null;
      });
  public static final Selector DAMAGE_SOURCE_ATTACKER =
      Selector.register(id("damage_source/attacker"), context -> {
        var s = context.requireParameter(LootContextParameters.DAMAGE_SOURCE).getAttacker();
        return s != null ? forEntity(s) : null;
      });

  public static final Selector RANDOM_PLAYER = Selector.register(id("random_player"), context -> {
    var l = context.getWorld().getServer().getPlayerManager().getPlayerList();
    if (l == null || l.isEmpty()) return null;
    return forEntity(Utilities.pickAtRandom(l));
  });

  public static void init() {}

  public static Identifier mc(String string) {
    return Identifier.of("minecraft", string);
  }

  public static ServerCommandSource forEntity(Entity entity) {
    return new ServerCommandSource(
        entity.getWorld().getServer(),
        entity.getPos(),
        new Vec2f(entity.getPitch(), entity.getYaw()),
        (ServerWorld) entity.getWorld(),
        4,
        entity.getNameForScoreboard(),
        entity.getName(),
        entity.getWorld().getServer(),
        entity);
  }
}
