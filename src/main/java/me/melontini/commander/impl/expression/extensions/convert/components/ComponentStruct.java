package me.melontini.commander.impl.expression.extensions.convert.components;

import com.ezylang.evalex.data.EvaluationValue;
import lombok.EqualsAndHashCode;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
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
    public boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        var component = registry.get(Identifier.validate(s).getOrThrow());
        if (component == null) return false;
        return map.contains(component);
    }

    @Override
    public EvaluationValue get(Object key) {
        if (!(key instanceof String s)) return EvaluationValue.NULL_VALUE;
        return convert(registry.get(Identifier.validate(s).getOrThrow()));
    }

    @Override
    public String toString() {
        return "ComponentStruct{" +
                "map=" + map +
                '}';
    }
}
