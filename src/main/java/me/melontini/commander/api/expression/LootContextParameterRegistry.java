package me.melontini.commander.api.expression;

import lombok.NonNull;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import net.minecraft.loot.context.LootContextParameter;
import org.jetbrains.annotations.ApiStatus;

/**
 * A registry for {@link LootContextParameter}. Parameters already come with an identifier, so specifying it separately is unnecessary.
 */
@ApiStatus.Experimental
public class LootContextParameterRegistry {

  public static void register(LootContextParameter<?> @NonNull ... parameters) {
    for (LootContextParameter<?> parameter : parameters) {
      ExtractionTypes.register(parameter);
    }
  }
}
