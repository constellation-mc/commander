package me.melontini.commander.impl.builtin.commands;

import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.Commander;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public interface StoreDataCommand extends Command {

    @Override
    default boolean execute(EventContext context) {
        var opt = selector().select(context);
        if (opt.isEmpty()) return false;

        var storage = target().select(opt.get()).getAttachedOrCreate(Commander.DATA_ATTACHMENT);
        storage.put(key(), asElement(context));
        return true;
    }

    NbtElement asElement(EventContext context);

    Target target();
    Selector.Conditioned selector();
    String key();

    enum Target {
        LEVEL(ServerCommandSource::getWorld),
        CHUNK(source -> source.getWorld().getChunk(BlockPos.ofFloored(source.getPosition()))),
        ENTITY(ServerCommandSource::getEntity),
        BLOCK_ENTITY(source -> source.getWorld().getBlockEntity(BlockPos.ofFloored(source.getPosition())));

        private final Function<ServerCommandSource, AttachmentTarget> function;

        Target(Function<ServerCommandSource, AttachmentTarget> function) {
            this.function = function;
        }

        public AttachmentTarget select(ServerCommandSource source) {
            return Objects.requireNonNull(this.function.apply(source), () -> "Source cannot be null! Type: %s".formatted(this.name().toLowerCase(Locale.ROOT)));
        }
    }
}
