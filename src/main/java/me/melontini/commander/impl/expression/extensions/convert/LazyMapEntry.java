package me.melontini.commander.impl.expression.extensions.convert;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import me.melontini.commander.api.expression.Expression;

@AllArgsConstructor
public class LazyMapEntry<K, V> implements Map.Entry<String, Expression.Result> {

  private final K key;
  private V value;

  private final transient Function<K, String> toString;

  @Override
  public String getKey() {
    return toString.apply(key);
  }

  @Override
  public Expression.Result getValue() {
    return Expression.Result.convert(value);
  }

  @Override
  public Expression.Result setValue(Expression.Result value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Map.Entry<?, ?> that)) return false;
    if (that instanceof LazyMapEntry<?, ?> lazy) {
      return Objects.equals(key, lazy.key) && Objects.equals(value, lazy.value);
    }
    return Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
  }

  @Override
  public int hashCode() {
    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
  }
}
