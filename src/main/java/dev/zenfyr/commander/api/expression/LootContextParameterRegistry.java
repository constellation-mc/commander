package dev.zenfyr.commander.api.expression;

import dev.zenfyr.commander.impl.expression.ExtractionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.jetbrains.annotations.ApiStatus;

/**
 * A registry for {@link LootContextParam}.
 */
@ApiStatus.Experimental
public class LootContextParameterRegistry {

  public static void register(ResourceLocation location, LootContextParam<?> parameters) {
    ExtractionTypes.register(location, parameters);
  }
}
