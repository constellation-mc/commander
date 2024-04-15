package me.melontini.commander.impl.util.eval;

import me.melontini.commander.impl.util.mappings.MappingKeeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectiveMapStructure implements Map<String, Object> {

    private static final Map<Class<?>, Map<String, Accessor>> MAPPINGS = new HashMap<>();
    private final Map<String, Accessor> mappings;
    private final Object object;

    public ReflectiveMapStructure(Object object) {
        this.object = object;

        Map<String, Accessor> map = MAPPINGS.get(object.getClass());
        if (map == null) {
            map = new HashMap<>();
            for (Field field : object.getClass().getFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                map.put(MappingKeeper.toNamed(field), field::get);
            }
            for (Method method : object.getClass().getMethods()) {
                if (Modifier.isStatic(method.getModifiers())) continue;
                if (method.getParameterCount() > 0) continue;
                if (method.getReturnType() == void.class) continue;

                String name = MappingKeeper.toNamed(method);

                map.put((name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) ? name :  "m_" + name, method::invoke);
            }
            MAPPINGS.put(object.getClass(), map);
        }
        this.mappings = map;
    }

    private interface Accessor {
        Object access(Object object) throws IllegalAccessException, InvocationTargetException;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.mappings.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        try {
            Accessor field = this.mappings.get(key);
            if (field == null) throw new RuntimeException("%s has no public field or method '%s'".formatted(this.object.getClass().getSimpleName(), key));
            return EvalUtils.CONFIGURATION.getEvaluationValueConverter().convertObject(field.access(this.object), EvalUtils.CONFIGURATION);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {

    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return this.mappings.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return String.valueOf(this.object);
    }
}
