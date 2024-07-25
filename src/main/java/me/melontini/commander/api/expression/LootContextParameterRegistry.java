package me.melontini.commander.api.expression;

import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import net.minecraft.loot.context.LootContextParameter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A registry for {@link LootContextParameter}. Parameters already come with an identifier, so specifying it separately is unnecessary.
 */
@ApiStatus.Experimental
public class LootContextParameterRegistry {

  public static void register(LootContextParameter<?> @NotNull ... parameters) {
    for (LootContextParameter<?> parameter : parameters) {
      ExtractionTypes.register(parameter);
    }
  }
}
