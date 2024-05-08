package me.melontini.commander.impl.expression.extensions.convert.nbt;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.google.common.collect.Lists;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.nbt.*;

import java.math.BigDecimal;

public class NbtConverter implements ConverterIfc {

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {

        if (object instanceof AbstractNbtNumber n) {
            return EvaluationValue.numberValue(BigDecimal.valueOf(n.doubleValue()));
        } else if (object instanceof NbtString n) {
            return EvaluationValue.stringValue(n.asString());
        } else if (object instanceof AbstractNbtList<?> n) {
            return EvaluationValue.arrayValue(Lists.transform(n, ProxyMap::convert));
        } else if (object instanceof NbtCompound n) {
            return EvaluationValue.structureValue(new NbtCompoundStruct(n));
        }

        throw illegalArgument(object);
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof NbtElement;
    }
}
