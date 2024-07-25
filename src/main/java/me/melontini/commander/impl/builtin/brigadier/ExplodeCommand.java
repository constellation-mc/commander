package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExplodeCommand {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(CommandManager.literal("cmd:explode")
        .requires(source -> source.hasPermissionLevel(2))
        .executes(context -> execute(
            context.getSource().getWorld(), null, context.getSource().getPosition(), 4, false))
        .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
            .executes(context -> execute(
                context.getSource().getWorld(),
                null,
                Vec3ArgumentType.getVec3(context, "pos"),
                4,
                false))
            .then(CommandManager.argument("power", FloatArgumentType.floatArg(0))
                .executes(context -> execute(
                    context.getSource().getWorld(),
                    null,
                    Vec3ArgumentType.getVec3(context, "pos"),
                    FloatArgumentType.getFloat(context, "power"),
                    false))
                .then(CommandManager.argument("fire", BoolArgumentType.bool())
                    .executes(context -> execute(
                        context.getSource().getWorld(),
                        null,
                        Vec3ArgumentType.getVec3(context, "pos"),
                        FloatArgumentType.getFloat(context, "power"),
                        BoolArgumentType.getBool(context, "fire"))))))
        .then(CommandManager.argument("entity", EntityArgumentType.entity())
            .executes(context -> execute(
                context.getSource().getWorld(),
                EntityArgumentType.getEntity(context, "entity"),
                EntityArgumentType.getEntity(context, "entity").getPos(),
                4,
                false))
            .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                .executes(context -> execute(
                    context.getSource().getWorld(),
                    EntityArgumentType.getEntity(context, "entity"),
                    Vec3ArgumentType.getVec3(context, "pos"),
                    4,
                    false))
                .then(CommandManager.argument("power", FloatArgumentType.floatArg(0))
                    .executes(context -> execute(
                        context.getSource().getWorld(),
                        EntityArgumentType.getEntity(context, "entity"),
                        Vec3ArgumentType.getVec3(context, "pos"),
                        FloatArgumentType.getFloat(context, "power"),
                        false))
                    .then(CommandManager.argument("fire", BoolArgumentType.bool())
                        .executes(context -> execute(
                            context.getSource().getWorld(),
                            EntityArgumentType.getEntity(context, "entity"),
                            Vec3ArgumentType.getVec3(context, "pos"),
                            FloatArgumentType.getFloat(context, "power"),
                            BoolArgumentType.getBool(context, "fire"))))))));
  }

  private static int execute(
      World world, @Nullable Entity entity, Vec3d vec, float power, boolean fire) {
    world.createExplosion(
        null,
        world.getDamageSources().explosion(null, entity),
        null,
        vec.getX(),
        vec.getY(),
        vec.getZ(),
        power,
        fire,
        World.ExplosionSourceType.TNT);
    return 1;
  }
}
