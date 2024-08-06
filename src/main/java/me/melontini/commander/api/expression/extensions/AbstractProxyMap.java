package me.melontini.commander.api.expression.extensions;

import java.util.AbstractMap;
import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see ProxyMap
 */
@ApiStatus.Experimental
public abstract class AbstractProxyMap extends AbstractMap<String, Expression.Result>
    implements ProxyMap {

  @Override
  public abstract boolean containsKey(Object key);

  @Override
  public abstract Expression.Result get(Object key);
}
