package me.melontini.commander.impl.expression.extensions.convert.states;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.extensions.AbstractProxyMap;
import me.melontini.commander.impl.expression.extensions.convert.LazyMapEntry;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

@EqualsAndHashCode(callSuper = false)
public final class StateStruct extends AbstractProxyMap {

  private final State<?, ?> state;
  private Set<Entry<String, Expression.Result>> entries;

  public StateStruct(State<?, ?> state) {
    this.state = state;
  }

  @Synchronized
  @Override
  public Set<Entry<String, Expression.Result>> entrySet() {
    if (entries == null) {
      this.entries = this.state.getEntries().entrySet().stream()
          .map(entry -> new LazyMapEntry<>(entry.getKey(), entry.getValue(), Property::getName))
          .collect(Collectors.toUnmodifiableSet());
    }
    return this.entries;
  }

  @Override
  public int size() {
    return state.getEntries().size();
  }

  @Override
  public boolean containsKey(Object o) {
    if (!(o instanceof String key)) return false;
    for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
      if (e.getKey().getName().equals(key)) return true;
    }
    return false;
  }

  @Override
  public Expression.Result get(Object o) {
    if (!(o instanceof String key)) return null;
    for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
      if (e.getKey().getName().equals(key)) return Expression.Result.convert(e.getValue());
    }
    return null;
  }
}
