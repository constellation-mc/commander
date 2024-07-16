package me.melontini.commander.impl.expression.extensions.convert;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
public class RegistryAccessStruct extends ProxyMap {

    private final Registry<?> registry;

    public RegistryAccessStruct(Registry<?> registry) {
        this.registry = registry;
    }

    @Override
    public boolean containsKey(String key) {
        return registry.containsId(new Identifier(key));
    }

    @Override
    public Object getValue(String key) {
        return registry.get(new Identifier(key));
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
