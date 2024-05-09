package me.melontini.commander.impl.expression.extensions.convert.nbt;

import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.nbt.NbtCompound;

public class NbtCompoundStruct extends ProxyMap {

    private final NbtCompound compound;

    public NbtCompoundStruct(NbtCompound compound) {
        this.compound = compound;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String s)) return false;
        return compound.contains(s);
    }

    @Override
    public EvaluationValue get(Object key) {
        if (!(key instanceof String s)) return EvaluationValue.nullValue();
        return convert(compound.get(s));
    }
}
