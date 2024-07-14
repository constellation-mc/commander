package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.data.EvaluationValue;
import lombok.EqualsAndHashCode;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
public class RegistryAccessStruct extends ProxyMap {

    private final Registry<?> registry;

    public RegistryAccessStruct(Registry<?> registry) {
        this.registry = registry;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        return registry.containsId(new Identifier(s));
    }

    @Override
    public EvaluationValue get(Object key) {
        if (!(key instanceof String s)) return EvaluationValue.NULL_VALUE;
        return convert(registry.get(new Identifier(s)));
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
