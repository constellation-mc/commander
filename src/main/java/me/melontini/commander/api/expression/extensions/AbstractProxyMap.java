package me.melontini.commander.api.expression.extensions;

import java.util.AbstractMap;
import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.ApiStatus;

/**
 * These maps must implement 3 methods: {@link #containsKey(Object)}, {@link #get(Object)} and {@link #entrySet()}.
 * It's recommended to lazily convert map entries, especially if the map is large.
 */
@ApiStatus.Experimental
public abstract class AbstractProxyMap extends AbstractMap<String, Expression.Result>
    implements ProxyMap {

  @Override
  public abstract boolean containsKey(Object key);

  @Override
  public abstract Expression.Result get(Object key);
}
