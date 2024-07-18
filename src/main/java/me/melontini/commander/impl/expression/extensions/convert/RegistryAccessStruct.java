package me.melontini.commander.impl.expression.extensions.convert;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import me.melontini.commander.impl.util.Identity;
import me.melontini.dark_matter.api.base.util.functions.Memoize;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@EqualsAndHashCode(callSuper = false)
public class RegistryAccessStruct extends ProxyMap {

    private static final Function<Identity<Registry<?>>, RegistryAccessStruct> CACHE = Memoize.lruFunction(eq -> new RegistryAccessStruct(eq.value()), 10);

    public synchronized static RegistryAccessStruct forRegistry(Registry<?> registry) {
        return CACHE.apply(new Identity<>(registry));
    }

    private final Registry<?> registry;
    private final Function<String, Object> cache;

    private RegistryAccessStruct(Registry<?> registry) {
        this.registry = registry;
        this.cache = Memoize.lruFunction(key -> registry.get(new Identifier(key)), 20);
    }

    @Override
    public boolean containsKey(String key) {
        return this.cache.apply(key) != null;
    }

    @Override
    public Object getValue(String key) {
        return this.cache.apply(key);
    }

    @Override
    public int size() {
        return registry.size();
    }

    @Override
    public String toString() {
        return String.valueOf(registry);
    }
}
