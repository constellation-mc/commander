package dev.zenfyr.commander.api.expression.extension;

import dev.zenfyr.commander.api.expression.Expression;
import java.util.Map;

/**
 * A special type of map which guarantees the type of {@code <String, Expression.Resul>}.<br/>
 * These maps must implement 3 methods: {@link #containsKey(Object)}, {@link #get(Object)} and {@link #entrySet()}.
 * It's recommended to lazily convert map entries, especially if the map is large.
 */
public interface ProxyMap extends Map<String, Expression.Result> {}
