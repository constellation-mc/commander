package me.melontini.commander.impl.util;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

public enum DataTarget {
  LEVEL(ServerCommandSource::getWorld),
  CHUNK(source -> source.getWorld().getChunk(BlockPos.ofFloored(source.getPosition()))),
  ENTITY(ServerCommandSource::getEntity),
  BLOCK_ENTITY(
      source -> source.getWorld().getBlockEntity(BlockPos.ofFloored(source.getPosition())));

  private final Function<ServerCommandSource, AttachmentTarget> function;

  DataTarget(Function<ServerCommandSource, AttachmentTarget> function) {
    this.function = function;
  }

  public AttachmentTarget select(ServerCommandSource source) {
    return Objects.requireNonNull(
        this.function.apply(source),
        () -> "Source cannot be null! Type: %s".formatted(this.name().toLowerCase(Locale.ROOT)));
  }
}
