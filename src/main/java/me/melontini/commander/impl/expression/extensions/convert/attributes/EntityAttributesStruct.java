package me.melontini.commander.impl.expression.extensions.convert.attributes;

import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;

public class EntityAttributesStruct extends ProxyMap {

    private final AttributeContainer container;

    public EntityAttributesStruct(AttributeContainer container) {
        this.container = container;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        var attr = Registries.ATTRIBUTE.get(new Identifier(s));
        if (attr == null) return false;
        return container.hasAttribute(attr);
    }

    @Override
    public EvaluationValue get(Object key) {
        if (!(key instanceof String s)) return EvaluationValue.nullValue();
        return EvaluationValue.numberValue(BigDecimal.valueOf(container.getValue(Registries.ATTRIBUTE.get(new Identifier(s)))));
    }

    @Override
    public String toString() {
        return String.valueOf(container.toNbt());
    }
}
