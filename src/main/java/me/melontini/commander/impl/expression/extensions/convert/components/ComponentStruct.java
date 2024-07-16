package me.melontini.commander.impl.expression.extensions.convert.components;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import me.melontini.commander.impl.Commander;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
public class ComponentStruct extends ProxyMap {

    private final ComponentMap map;
    private final Registry<ComponentType<?>> registry = Commander.get().currentServer().getRegistryManager().get(RegistryKeys.DATA_COMPONENT_TYPE);

    public ComponentStruct(ComponentMap map) {
        this.map = map;
    }

    @Override
    public boolean containsKey(String key) {
        var component = registry.get(Identifier.validate(key).getOrThrow());
        if (component == null) return false;
        return map.contains(component);
    }

    @Override
    public Object getValue(String key) {
        return map.get(registry.get(Identifier.validate(key).getOrThrow()));
    }

    @Override
    public String toString() {
        return "ComponentStruct{" +
                "map=" + map +
                '}';
    }
}
