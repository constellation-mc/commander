package me.melontini.commander.impl.builtin.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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

    private static final DynamicCommandExceptionType NO_BE_EXCEPTION = new DynamicCommandExceptionType((a) -> TextUtil.literal("No block entity at position %s".formatted(a)));
    private static final DynamicCommandExceptionType NO_KEY_EXCEPTION = new DynamicCommandExceptionType((a) -> TextUtil.literal("No such key '%s' in target".formatted(a)));
    private static final SimpleCommandExceptionType WRONG_DATA_TYPE_EXCEPTION = new SimpleCommandExceptionType(TextUtil.literal("Nbt element must be a string or a number!"));

    private static final Map<Target, Supplier<RequiredArgumentBuilder<ServerCommandSource, ?>>> ARGS = Map.of(
            Target.LEVEL, () -> null,
            Target.CHUNK, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos()),
            Target.ENTITY, () -> CommandManager.argument("entity", EntityArgumentType.entity()),
            Target.BLOCK_ENTITY, () -> CommandManager.argument("position", BlockPosArgumentType.blockPos())
    );

    private static final Map<Target, PseudoFunction<CommandContext<ServerCommandSource>, AttachmentTarget>> TO_TARGET = Map.of(
            Target.LEVEL, ctx -> ctx.getSource().getWorld(),
            Target.CHUNK, ctx -> ctx.getSource().getWorld().getChunk(BlockPosArgumentType.getBlockPos(ctx, "position")),
            Target.ENTITY, ctx -> EntityArgumentType.getEntity(ctx, "entity"),
            Target.BLOCK_ENTITY, ctx -> {
                var pos = BlockPosArgumentType.getBlockPos(ctx, "position");
                var be = ctx.getSource().getWorld().getBlockEntity(pos);
                if (be == null)
                    throw NO_BE_EXCEPTION.create("[%s, %s, %s]".formatted(pos.getX(), pos.getY(), pos.getZ()));
                return be;
            }
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cmd = CommandManager.literal("cmd:data").requires(source -> source.hasPermissionLevel(2));

        var read = CommandManager.literal("read");
        for (Target value : Target.values()) {
            var start = CommandManager.literal(value.name().toLowerCase(Locale.ROOT));
            var keyArg = CommandManager.argument("key", StringArgumentType.string())
                    .executes(context -> readValue(context.getSource(), TO_TARGET.get(value).apply(context), StringArgumentType.getString(context, "key")));

            var args = ARGS.get(value).get();
            if (args != null) args.then(keyArg);
            else args = keyArg;

            read.then(start.then(args));
        }
        cmd.then(read);

        var write = CommandManager.literal("write");
        for (Target value : Target.values()) {
            var start = CommandManager.literal(value.name().toLowerCase(Locale.ROOT));
            var keyArg = CommandManager.argument("key", StringArgumentType.string()).then(CommandManager.argument("data", NbtElementArgumentType.nbtElement())
                    .executes(context -> writeValue(context.getSource(), TO_TARGET.get(value).apply(context), StringArgumentType.getString(context, "key"), NbtElementArgumentType.getNbtElement(context, "data"))));

            var args = ARGS.get(value).get();
            if (args != null) args.then(keyArg);
            else args = keyArg;

            write.then(start.then(args));
        }
        cmd.then(write);

        var remove = CommandManager.literal("remove");
        for (Target value : Target.values()) {
            var start = CommandManager.literal(value.name().toLowerCase(Locale.ROOT));
            var keyArg = CommandManager.argument("key", StringArgumentType.string())
                    .executes(context -> removeValue(context.getSource(), TO_TARGET.get(value).apply(context), StringArgumentType.getString(context, "key")));

            var args = ARGS.get(value).get();
            if (args != null) args.then(keyArg);
            else args = keyArg;

            remove.then(start.then(args));
        }
        cmd.then(remove);

        dispatcher.register(cmd);
    }

    private static int removeValue(ServerCommandSource source, AttachmentTarget target, String key) {
        var nbt = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT);
        if (nbt.contains(key)) {
            nbt.remove(key);
            source.sendFeedback(() -> TextUtil.literal("Successfully removed key %s from target!".formatted(key)), false);
            return 1;
        }
        source.sendFeedback(() -> TextUtil.literal("Target has no %s key!".formatted(key)), false);
        return 0;
    }

    public static int writeValue(ServerCommandSource source, AttachmentTarget target, String key, NbtElement element) throws CommandSyntaxException {
        var r = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT);
        if (element instanceof AbstractNbtNumber || element instanceof NbtString) {
            r.put(key, element);
            source.sendFeedback(() -> TextUtil.literal("Successfully wrote value %s to target!".formatted(element)), false);
            return 1;
        }
        throw WRONG_DATA_TYPE_EXCEPTION.create();
    }

    public static int readValue(ServerCommandSource source, AttachmentTarget target, String key) throws CommandSyntaxException {
        var r = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT).get(key);
        if (r == null) {
            throw NO_KEY_EXCEPTION.create(key);
        }
        source.sendFeedback(() -> TextUtil.literal(r.asString()), false);
        return 1;
    }

    private interface PseudoFunction<T, R> {
        R apply(T t) throws CommandSyntaxException;
    }
}
