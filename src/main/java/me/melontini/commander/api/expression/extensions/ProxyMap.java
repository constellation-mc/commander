package me.melontini.commander.api.expression.extensions;

import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ApiStatus.Experimental
public abstract class ProxyMap implements Map<String, Expression.Result> {

    protected abstract boolean containsKey(String key);
    protected abstract Object getValue(String key);

    @Override
    public final Expression.Result get(Object key) {
        if (!(key instanceof String s)) return Expression.Result.convert(null);
        return Expression.Result.convert(getValue(s));
    }

    @Override
    public final boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        return this.containsKey(s);
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
    public Expression.Result put(String key, Expression.Result value) {
        throw new IllegalStateException();
    }

    @Override
    public Expression.Result remove(Object key) {
        throw new IllegalStateException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Expression.Result> m) {
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
    public Collection<Expression.Result> values() {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Set<Entry<String, Expression.Result>> entrySet() {
        throw new IllegalStateException();
    }
}
