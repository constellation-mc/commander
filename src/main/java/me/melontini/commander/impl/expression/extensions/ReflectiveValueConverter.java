package me.melontini.commander.impl.expression.extensions;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.*;
import me.melontini.commander.impl.expression.extensions.convert.LazyArrayConverter;
import me.melontini.commander.impl.expression.extensions.convert.nbt.NbtConverter;

import java.util.Arrays;
import java.util.List;

public class ReflectiveValueConverter implements EvaluationValueConverterIfc {

    static List<ConverterIfc> converters = Arrays.asList(
                    new NumberConverter(),
                    new StringConverter(),
                    new BooleanConverter(),
                    new DateTimeConverter(),
                    new DurationConverter(),
                    new ExpressionNodeConverter(),
                    new NbtConverter(),
                    new LazyArrayConverter());

    @Override
    public EvaluationValue convertObject(Object object, ExpressionConfiguration configuration) {
        if (object == null) return EvaluationValue.nullValue();
        if (object instanceof EvaluationValue value) return value;
        if (object instanceof ProxyMap map) return EvaluationValue.structureValue(map); //Proxy maps convert all of their outputs.

        for (ConverterIfc converter : converters) {
            if (converter.canConvert(object)) return converter.convert(object, configuration);
        }

        return EvaluationValue.structureValue(new ReflectiveMapStructure(object));
    }
}
