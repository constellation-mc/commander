package me.melontini.commander.api.expression.extensions;

import me.melontini.commander.api.expression.Expression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A contextual data accessor. Can be implemented on objects to automatically support expression access.<br/>
 * You might prefer using {@link ProxyMap} if your implementation is backed by a map.
 */
@ApiStatus.OverrideOnly
@ApiStatus.Experimental
public interface CustomDataAccessor {

  /**
   * Returns {@link Expression.Result} or null if there's no such field.
   * {@code null} and {@link Expression.Result#NULL} mean different things here:
   * <ul>
   *     <li>{@link Expression.Result#NULL} represents a <b>present</b> value which is null.</li>
   *     <li>{@code null} represents no value.</li>
   * </ul>
   * @param variable The requested variable or field.
   * @param context The current loot context.
   * @return {@link Expression.Result} or null if there's no such field.
   */
  @Nullable Expression.Result getExpressionData(String variable, LootContext context) throws Exception;
}
