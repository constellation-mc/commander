package me.melontini.commander.impl.expression.extensions.convert.states;

import com.ezylang.evalex.data.EvaluationValue;
import lombok.EqualsAndHashCode;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

@EqualsAndHashCode(callSuper = false)
public class StateStruct extends ProxyMap {

    private final State<?, ?> state;

    public StateStruct(State<?, ?> state) {
        this.state = state;
    }

    @Override
    public boolean containsKey(Object key) {
        for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
            if (e.getKey().getName().equals(key)) return true;
        }
        return false;
    }

    @Override
    public EvaluationValue get(Object key) {
        for (Entry<Property<?>, Comparable<?>> e : state.getEntries().entrySet()) {
            if (e.getKey().getName().equals(key)) return convert(e.getValue());
        }
        return EvaluationValue.NULL_VALUE;
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
