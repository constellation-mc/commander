package me.melontini.commander.api.expression.extensions;

import me.melontini.commander.api.expression.Expression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A contextual data accessor. Can be implemented on objects to automatically support expression access.
 */
@ApiStatus.Experimental
public interface CustomDataAccessor {

  @Nullable Expression.Result getExpressionData(String variable, LootContext context) throws Exception;
}
