package me.melontini.commander.impl.expression.extensions.convert.states;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

@EqualsAndHashCode(callSuper = false)
public class StateStruct extends ProxyMap {

    private final State<?, ?> state;

    public StateStruct(State<?, ?> state) {
        this.state = state;
    }

    @Override
    public boolean containsKey(String key) {
        for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
            if (e.getKey().getName().equals(key)) return true;
        }
        return false;
    }

    @Override
    public Object getValue(String key) {
        for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
            if (e.getKey().getName().equals(key)) return e.getValue();
        }
        return null;
    }

    @Override
    public int size() {
        return state.getEntries().size();
    }

    @Override
    public String toString() {
        return String.valueOf(state.getEntries());
    }
}

