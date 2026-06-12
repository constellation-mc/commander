package dev.zenfyr.commander.api.expression.extension;

import dev.zenfyr.commander.api.expression.Expression;
import java.util.AbstractMap;
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
