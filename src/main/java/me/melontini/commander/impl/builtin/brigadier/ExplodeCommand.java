package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExplodeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("cmd:explode").requires(source -> source.hasPermissionLevel(2))
                .executes(context -> execute(context.getSource().getWorld(), context.getSource().getPosition(), 4))
                .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                        .executes(context -> execute(context.getSource().getWorld(), Vec3ArgumentType.getVec3(context, "pos"), 4))
                        .then(CommandManager.argument("power", FloatArgumentType.floatArg(0))
                                .executes(context -> execute(context.getSource().getWorld(), Vec3ArgumentType.getVec3(context, "pos"), FloatArgumentType.getFloat(context, "power"))))));
    }

    private static int execute(World world, Vec3d vec, float power) {
        world.createExplosion(null, vec.getX(), vec.getY(), vec.getZ(),
                power, World.ExplosionSourceType.TNT);
        return 1;
    }
}
