package me.melontini.commander.impl.expression.extensions.convert.attributes;

import com.ezylang.evalex.data.EvaluationValue;
import lombok.EqualsAndHashCode;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
public class EntityAttributesStruct extends ProxyMap {

    private final AttributeContainer container;

    public EntityAttributesStruct(AttributeContainer container) {
        this.container = container;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        var attr = Registries.ATTRIBUTE.getEntry(Identifier.validate(s).getOrThrow());
        return attr.filter(container::hasAttribute).isPresent();
    }

    @Override
    public EvaluationValue get(Object key) {
        if (!(key instanceof String s)) return EvaluationValue.NULL_VALUE;
        return EvaluationValue.numberValue(BigDecimal.valueOf(container.getValue(Registries.ATTRIBUTE.getEntry(Identifier.of(s)).orElseThrow())));
    }

    @Override
    public String toString() {
        return "EntityAttributesStruct{" +
                "container=" + container.toNbt() +
                '}';
    }
}
