package me.melontini.commander.impl.expression.extensions.convert.attributes;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;


@EqualsAndHashCode(callSuper = false)
public class EntityAttributesStruct extends ProxyMap {

    private final AttributeContainer container;

    public EntityAttributesStruct(AttributeContainer container) {
        this.container = container;
    }

    @Override
    public boolean containsKey(String key) {
        var attr = Registries.ATTRIBUTE.get(new Identifier(key));
        if (attr == null) return false;
        return container.hasAttribute(attr);
    }

    @Override
    public Object getValue(String key) {
        return container.getValue(Registries.ATTRIBUTE.get(new Identifier(key)));
    }

    @Override
    public String toString() {
        return String.valueOf(container.toNbt());
    }
}
