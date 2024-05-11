package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.builtin.commands.StoreDataCommand.Target;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class DataCommand {

    private static final Map<Target, Supplier<RequiredArgumentBuilder<ServerCommandSource, ?>>> READ = Map.of(
            Target.LEVEL, () -> CommandManager.argument("key", StringArgumentType.string())
                    .executes(context -> readValue(context.getSource(), context.getSource().getWorld(), StringArgumentType.getString(context, "key"))),

            Target.CHUNK, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .executes(context -> readValue(context.getSource(), context.getSource().getWorld().getChunk(BlockPosArgumentType.getBlockPos(context, "position")), StringArgumentType.getString(context, "key")))),

            Target.ENTITY, () -> CommandManager.argument("entity", EntityArgumentType.entity())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .executes(context -> readValue(context.getSource(), EntityArgumentType.getEntity(context, "entity"), StringArgumentType.getString(context, "key")))),

            Target.BLOCK_ENTITY, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .executes(context -> {
                                var pos = BlockPosArgumentType.getBlockPos(context, "position");
                                var be = context.getSource().getWorld().getBlockEntity(pos);
                                if (be == null) {
                                    context.getSource().sendError(TextUtil.literal("No block entity at position '%s %s %s'".formatted(pos.getX(), pos.getY(), pos.getZ())));
                                    return 0;
                                }
                                return readValue(context.getSource(), be, StringArgumentType.getString(context, "key"));
                            }))
    );

    private static final Map<Target, Supplier<RequiredArgumentBuilder<ServerCommandSource, ?>>> WRITE = Map.of(
            Target.LEVEL, () -> CommandManager.argument("key", StringArgumentType.string())
                    .then(CommandManager.argument("data", NbtElementArgumentType.nbtElement())
                            .executes(context -> writeValue(context.getSource(), context.getSource().getWorld(), StringArgumentType.getString(context, "key"), NbtElementArgumentType.getNbtElement(context, "data")))),

            Target.CHUNK, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .then(CommandManager.argument("data", NbtElementArgumentType.nbtElement())
                                    .executes(context -> writeValue(context.getSource(), context.getSource().getWorld().getChunk(BlockPosArgumentType.getBlockPos(context, "position")), StringArgumentType.getString(context, "key"), NbtElementArgumentType.getNbtElement(context, "data"))))),

            Target.ENTITY, () -> CommandManager.argument("entity", EntityArgumentType.entity())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .then(CommandManager.argument("data", NbtElementArgumentType.nbtElement())
                                    .executes(context -> writeValue(context.getSource(), EntityArgumentType.getEntity(context, "entity"), StringArgumentType.getString(context, "key"), NbtElementArgumentType.getNbtElement(context, "data"))))),

            Target.BLOCK_ENTITY, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("key", StringArgumentType.string())
                            .then(CommandManager.argument("data", NbtElementArgumentType.nbtElement())
                                    .executes(context -> {
                                        var pos = BlockPosArgumentType.getBlockPos(context, "position");
                                        var be = context.getSource().getWorld().getBlockEntity(pos);
                                        if (be == null) {
                                            context.getSource().sendError(TextUtil.literal("No block entity at position '%s %s %s'".formatted(pos.getX(), pos.getY(), pos.getZ())));
                                            return 0;
                                        }
                                        return writeValue(context.getSource(), be, StringArgumentType.getString(context, "key"), NbtElementArgumentType.getNbtElement(context, "data"));
                                    })))

    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cmd = CommandManager.literal("cmd:data").requires(source -> source.hasPermissionLevel(2));

        var read = CommandManager.literal("read");
        for (Target value : Target.values()) {
            read.then(CommandManager.literal(value.name().toLowerCase(Locale.ROOT)).then(READ.get(value).get()));
        }
        cmd.then(read);

        var write = CommandManager.literal("write");
        for (Target value : Target.values()) {
            write.then(CommandManager.literal(value.name().toLowerCase(Locale.ROOT)).then(WRITE.get(value).get()));
        }
        cmd.then(write);

        dispatcher.register(cmd);
    }

    public static int writeValue(ServerCommandSource source, AttachmentTarget target, String key, NbtElement element) {
        var r = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT);
        if (element instanceof AbstractNbtNumber || element instanceof NbtString) {
            r.put(key, element);
            return 1;
        }
        source.sendError(TextUtil.literal("Nbt element must be a string or a number!"));
        return 0;
    }

    public static int readValue(ServerCommandSource source, AttachmentTarget target, String key) {
        var r = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT).get(key);
        if (r == null) {
            source.sendError(TextUtil.literal("No such key '%s'".formatted(key)));
            return 0;
        }
        source.sendMessage(TextUtil.literal(r.asString()));
        return 1;
    }
}
