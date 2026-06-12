package dev.zenfyr.commander.impl.expression;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import dev.zenfyr.pulsar.codec.ExtraCodecs;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.jetbrains.annotations.Nullable;

public class ExtractionTypes {

  private static final BiMap<ResourceLocation, LootContextParam<?>> KNOWN_PARAMETERS =
      HashBiMap.create();
  public static final Codec<LootContextParam<?>> CODEC =
      ExtraCodecs.mapLookup(ResourceLocation.CODEC, KNOWN_PARAMETERS);

  public static @Nullable LootContextParam<?> getParameter(ResourceLocation identifier) {
    return KNOWN_PARAMETERS.get(identifier);
  }

  public static void register(ResourceLocation location, @NonNull LootContextParam<?> parameter) {
    var old = KNOWN_PARAMETERS.put(location, parameter);
    if (old != null)
      throw new IllegalStateException("Already registered context %s".formatted(location));
  }
}
