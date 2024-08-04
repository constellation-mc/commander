package me.melontini.commander.impl.util;

// Used by registry caches
public record Identity<V>(V value) {

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Identity<?> identity)) return false;
    return value == identity.value;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(value);
  }
}
