package me.melontini.commander.api.expression.extensions;

import java.util.Map;
import me.melontini.commander.api.expression.Expression;

/**
 * A special type of map which guarantees the type of {@code <String, Expression.Resul>}.<br/>
 * These maps must implement 3 methods: {@link #containsKey(Object)}, {@link #get(Object)} and {@link #entrySet()}.
 * It's recommended to lazily convert map entries, especially if the map is large.
 */
public interface ProxyMap extends Map<String, Expression.Result> {}
