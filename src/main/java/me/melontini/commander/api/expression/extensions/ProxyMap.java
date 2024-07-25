package me.melontini.commander.api.expression.extensions;

import java.util.Map;
import me.melontini.commander.api.expression.Expression;

/**
 * A special type of map which guarantees the type of {@code <String, Expression.Resul>}.<br/>
 */
public interface ProxyMap extends Map<String, Expression.Result> {}
