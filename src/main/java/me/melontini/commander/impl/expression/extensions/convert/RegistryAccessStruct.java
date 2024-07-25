package me.melontini.commander.impl.expression.extensions.convert;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Synchronized;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.extensions.AbstractProxyMap;
import me.melontini.commander.impl.util.Identity;
import me.melontini.dark_matter.api.base.util.functions.Memoize;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
public class RegistryAccessStruct extends AbstractProxyMap {

  private static final Object CACHE_LOCK = new Object();
  private static Function<Identity<Registry<?>>, RegistryAccessStruct> CACHE;

  static {
    resetCache();
  }

  public static void resetCache() {
    synchronized (CACHE_LOCK) {
      CACHE = Memoize.lruFunction(identity -> new RegistryAccessStruct(identity.value()), 10);
    }
  }

  public static synchronized RegistryAccessStruct forRegistry(Registry<?> registry) {
    synchronized (CACHE_LOCK) {
      return CACHE.apply(new Identity<>(registry));
    }
  }

  private final Registry<?> registry;

  @Getter
  private final Function<String, Object> cache;

  private Set<Entry<String, Expression.Result>> entries;

  private RegistryAccessStruct(Registry<?> registry) {
    this.registry = registry;
    this.cache = Memoize.lruFunction(key -> registry.get(new Identifier(key)), 20);
  }

  @Override
  public boolean containsKey(Object o) {
    if (!(o instanceof String key)) return false;
    return this.cache.apply(key) != null;
  }

  @Override
  public Expression.Result get(Object o) {
    if (!(o instanceof String key)) return null;
    return Expression.Result.convert(this.cache.apply(key));
  }

  @Override
  public int size() {
    return registry.size();
  }

  @Synchronized
  @NotNull @Override
  public Set<Entry<String, Expression.Result>> entrySet() {
    if (entries == null) {
      this.entries = registry.getEntrySet().stream()
          .map(entry -> new LazyMapEntry<>(
              entry.getKey(), entry.getValue(), key -> key.getValue().toString()))
          .collect(Collectors.toUnmodifiableSet());
    }
    return entries;
  }

  @Override
  public String toString() {
    return String.valueOf(registry);
  }
}
