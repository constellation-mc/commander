package me.melontini.commander.impl.expression.extensions;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

@Log4j2
@EqualsAndHashCode(callSuper = false)
public class ReflectiveMapStructure implements DataAccessorIfc {

  private static final Map<Class<?>, Struct> MAPPINGS = new Reference2ReferenceOpenHashMap<>();
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  static {
    DefaultCustomFields.init();
  }

  @EqualsAndHashCode.Exclude
  private final Struct mappings;

  private final Object object;

  public ReflectiveMapStructure(Object object) {
    this.object = object;
    this.mappings = getAccessors(object.getClass());
  }

  public static <C> void addField(
      Class<C> cls, String name, BiFunction<C, LootContext, Object> accessor) {
    ReflectiveMapStructure.getAccessors(cls)
        .addAccessor(name, (BiFunction<Object, LootContext, Object>) accessor);
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

  private static BiFunction<Object, LootContext, Object> methodAccessor(Method method) {
    try {
      var handle = LOOKUP.unreflect(method);
      CallSite getterSite = LambdaMetafactory.metafactory(
          LOOKUP,
          "apply",
          MethodType.methodType(Function.class),
          MethodType.methodType(Object.class, Object.class),
          handle,
          handle.type().wrap());
      var getter = (Function<Object, Object>) getterSite.getTarget().invoke();
      return (object1, lootContext) -> getter.apply(object1);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    if (this.mappings.isInvalid(variable)) return null;

    var cache = this.mappings.getAccessor(variable);
    if (cache != null)
      return ReflectiveValueConverter.convert(
          cache.apply(this.object, (LootContext) context.context()[0]));

    var accessor = findFieldOrMethod(this.object.getClass(), variable);
    if (accessor == null) {
      this.mappings.invalidate(variable);
      return null;
    }

    synchronized (MAPPINGS) {
      getAccessors(accessor.left()).addAccessor(variable, accessor.right());
    }
    return ReflectiveValueConverter.convert(
        accessor.right().apply(this.object, (LootContext) context.context()[0]));
  }

  private static @Nullable Tuple<Class<?>, BiFunction<Object, LootContext, Object>>
      findFieldOrMethod(Class<?> cls, String name) {
    var keeper = Commander.get().mappingKeeper();
    String mapped;
    Class<?> target = cls;
    do {
      if ((mapped = keeper.getFieldOrMethod(target, name)) != null)
        return findAccessor(target, mapped);
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

  @Nullable private static Tuple<Class<?>, BiFunction<Object, LootContext, Object>> findAccessor(
      @NonNull Class<?> cls, String mapped) {
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
      return Tuple.of(field.getDeclaringClass(), (o, context) -> {
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
  public String toString() {
    return String.valueOf(this.object);
  }

  static final class Struct {
    private Map<String, BiFunction<Object, LootContext, Object>> accessors;
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

    public @Nullable BiFunction<Object, LootContext, Object> getAccessor(String key) {
      return this.accessors == null ? null : this.accessors.get(key);
    }

    @Synchronized
    public void addAccessor(String key, BiFunction<Object, LootContext, Object> accessor) {
      if (this.accessors == null) this.accessors = new Object2ReferenceOpenHashMap<>();
      this.accessors.put(key, accessor);

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
