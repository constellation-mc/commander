package me.melontini.commander.impl.expression.extensions;

import me.melontini.commander.impl.expression.EvalUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class ProxyMap implements Map<String, Object> {

    public static Object convert(Object o) {
        return EvalUtils.CONFIGURATION.getEvaluationValueConverter().convertObject(o, EvalUtils.CONFIGURATION);
    }

    @Override
    public int size() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isEmpty() {
        throw new IllegalStateException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new IllegalStateException();
    }

    @Nullable @Override
    public Object put(String key, Object value) {
        throw new IllegalStateException();
    }

    @Override
    public Object remove(Object key) {
        throw new IllegalStateException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        throw new IllegalStateException();
    }

    @Override
    public void clear() {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Set<String> keySet() {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Collection<Object> values() {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new IllegalStateException();
    }
}
