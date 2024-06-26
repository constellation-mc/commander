package me.melontini.commander.impl.expression.extensions;

import com.ezylang.evalex.data.EvaluationValue;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.CmdEvalException;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

@Log4j2
public class ReflectiveMapStructure extends ProxyMap {

    private static final Map<Class<?>, Struct> MAPPINGS = new Reference2ReferenceOpenHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    static {
        CustomFields.init();
    }

    private final Struct mappings;
    private final Object object;

    public ReflectiveMapStructure(Object object) {
        this.object = object;
        this.mappings = getAccessors(object.getClass());
    }

    public static <C> void addField(Class<C> cls, String name, Function<C, Object> accessor) {
        ReflectiveMapStructure.getAccessors(cls).addAccessor(name, (Function<Object, Object>) accessor);
    }

    private static Struct getAccessors(Class<?> cls) {
        Struct map = MAPPINGS.get(cls);
        if (map != null) return map;

        synchronized (MAPPINGS) {
            Struct struct = new Struct();

            for (Class<?> anInterface : cls.getInterfaces()) {
                getAccessors(anInterface).addListener(struct);
            }
            Class<?> target = cls;
            while ((target = target.getSuperclass()) != null) {
                getAccessors(target).addListener(struct);
                for (Class<?> anInterface : cls.getInterfaces()) {
                    getAccessors(anInterface).addListener(struct);
                }
            }

            MAPPINGS.put(cls, struct);
            return struct;
        }
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

    @Override
    public boolean containsKey(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof String key)) return false;

        if (this.mappings.isInvalid(key)) return false;

        var cache = this.mappings.getAccessor(key);
        if (cache != null) return true;

        var accessor = findFieldOrMethod(this.object.getClass(), key);
        if (accessor == null) {
            this.mappings.invalidate(key);
            return false;
        }

        synchronized (MAPPINGS) {
            getAccessors(accessor.left()).addAccessor(key, accessor.right());
        }
        return true;
    }

    private static @Nullable Tuple<Class<?>, Function<Object, Object>> findFieldOrMethod(Class<?> cls, String name) {
        var keeper = Commander.get().mappingKeeper();
        String mapped;
        Class<?> target = cls;
        do {
            if ((mapped = keeper.getFieldOrMethod(target, name)) != null) return findAccessor(target, mapped);
            var targetItfs = target.getInterfaces();
            if (targetItfs.length == 0) continue;

            Queue<Class<?>> interfaces = new ArrayDeque<>(List.of(targetItfs));
            while (!interfaces.isEmpty()) {
                var itf = interfaces.poll();

                if ((mapped = keeper.getFieldOrMethod(itf, name)) != null) return findAccessor(itf, mapped);
                if ((targetItfs = itf.getInterfaces()).length > 0) interfaces.addAll(List.of(targetItfs));
            }
        } while ((target = target.getSuperclass()) != null);
        return findAccessor(cls, name);
    }

    @Nullable private static Tuple<Class<?>, Function<Object, Object>> findAccessor(@NonNull Class<?> cls, String mapped) {
        for (Method method : cls.getMethods()) {
            if (!method.getName().equals(mapped)) continue;
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (method.getParameterCount() > 0) continue;
            if (method.getReturnType() == void.class) continue;

            return Tuple.of(method.getDeclaringClass(), methodAccessor(method));
        }

        for (Field field : cls.getFields()) {
            if (!field.getName().equals(mapped)) continue;
            if (Modifier.isStatic(field.getModifiers())) continue;
            return Tuple.of(field.getDeclaringClass(), o -> {
                try {
                    return field.get(o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return null;
    }

    @Override
    public EvaluationValue get(Object key) {
        try {
            Function<Object, Object> field = this.mappings.getAccessor((String) key);
            if (field == null) throw new RuntimeException("%s has no public field or method '%s'".formatted(this.object.getClass().getSimpleName(), key));
            return convert(field.apply(this.object));
        } catch (Exception e) {
            throw new CmdEvalException(Objects.requireNonNullElse(e.getMessage(), "Failed to reflectively access member!"));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.object);
    }

    static final class Struct {
        private Map<String, Function<Object, Object>> accessors;
        private Set<String> invalid;
        private Set<Struct> consumers;

        public boolean isInvalid(String key) {
            return this.invalid != null && this.invalid.contains(key);
        }

        @Synchronized
        public void invalidate(String key) {
            if (this.invalid == null) this.invalid = new ObjectOpenHashSet<>();
            this.invalid.add(key);
        }

        public @Nullable Function<Object, Object> getAccessor(String key) {
            return this.accessors == null ? null : this.accessors.get(key);
        }

        @Synchronized
        public void addAccessor(String key, Function<Object, Object> accessor) {
            if (this.accessors == null) this.accessors = new Object2ReferenceOpenHashMap<>();
            this.accessors.putIfAbsent(key, accessor);

            if (this.consumers == null) return;
            for (Struct consumer : consumers) {
                consumer.addAccessor(key, accessor);
            }
        }

        @Synchronized
        public void addListener(Struct other) {
            if (this.consumers == null) this.consumers = new ReferenceOpenHashSet<>();
            this.consumers.add(other);
            if (this.accessors != null) this.accessors.forEach(other::addAccessor);
        }

        @Override
        public String toString() {
            return String.valueOf(this.accessors);
        }
    }
}
