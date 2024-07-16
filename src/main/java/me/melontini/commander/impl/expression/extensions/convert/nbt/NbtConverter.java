package me.melontini.commander.impl.expression.extensions.convert.nbt;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.conversion.NumberConverter;
import com.google.common.collect.Lists;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.nbt.*;

public class NbtConverter implements ConverterIfc {

    private final NumberConverter converter = new NumberConverter();

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {

        if (object instanceof AbstractNbtNumber n) {
            return converter.convert(n.numberValue(), configuration);
        } else if (object instanceof NbtString n) {
            return EvaluationValue.stringValue(n.asString());
        } else if (object instanceof AbstractNbtList<?> n) {
            return EvaluationValue.arrayValue(Lists.transform(n, ReflectiveValueConverter::convert));
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
