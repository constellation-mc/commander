package me.melontini.commander.impl.util.mappings;

public interface AmbiguousRemapper {
    String getFieldOrMethod(Class<?> cls, String name);
}
