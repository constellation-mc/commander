package me.melontini.commander.impl.util.eval;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.melontini.commander.impl.util.mappings.MappingKeeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ReflectiveMapStructure implements Map<String, Object> {

    private static final Map<Class<?>, Map<String, Accessor>> MAPPINGS = new Reference2ReferenceOpenHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final Map<String, Accessor> mappings;
    private final Object object;

    public ReflectiveMapStructure(Object object) {
        this.object = object;
        this.mappings = getAccessors(object.getClass());
    }

    public static Map<String, Accessor> getAccessors(Class<?> cls) {
        Map<String, Accessor> map = MAPPINGS.get(cls);
        if (map == null) {
            synchronized (MAPPINGS) {
                map = new Object2ReferenceOpenHashMap<>();
                for (Field field : cls.getFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    map.put(MappingKeeper.toNamed(field), field::get);
                }
                for (Method method : cls.getMethods()) {
                    if (Modifier.isStatic(method.getModifiers())) continue;
                    if (method.getParameterCount() > 0) continue;
                    if (method.getReturnType() == void.class) continue;

                    var accessor = methodAccessor(method);
                    map.put(MappingKeeper.toNamed(method), accessor::apply);
                }
                MAPPINGS.put(cls, map);
            }
        }
        return map;
    }

    private static Function<Object, Object> methodAccessor(Method method) {
        try {
            var handle = LOOKUP.unreflect(method);
            CallSite getterSite = LambdaMetafactory.metafactory(LOOKUP,
                        "apply",
                        MethodType.methodType(Function.class),
                        MethodType.methodType(Object.class, Object.class),
                        handle, handle.type().wrap());
            return (Function<Object, Object>) getterSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public interface Accessor {
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
