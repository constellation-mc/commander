package me.melontini.commander.impl.util.mappings;

import org.jetbrains.annotations.Nullable;

public interface AmbiguousRemapper {
    @Nullable String getFieldOrMethod(Class<?> cls, String name);
}
