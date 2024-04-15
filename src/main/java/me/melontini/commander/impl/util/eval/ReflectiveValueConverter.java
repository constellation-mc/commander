package me.melontini.commander.impl.util.eval;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.*;

import java.util.Arrays;
import java.util.List;

public class ReflectiveValueConverter implements EvaluationValueConverterIfc {

    static List<ConverterIfc> converters =
            Arrays.asList(
                    new NumberConverter(),
                    new StringConverter(),
                    new BooleanConverter(),
                    new DateTimeConverter(),
                    new DurationConverter(),
                    new ExpressionNodeConverter(),
                    new ArrayConverter(),
                    new StructureConverter());

    @Override
    public EvaluationValue convertObject(Object object, ExpressionConfiguration configuration) {

        if (object == null) {
            return EvaluationValue.nullValue();
        }

        if (object instanceof EvaluationValue) {
            return (EvaluationValue) object;
        }

        for (ConverterIfc converter : converters) {
            if (converter.canConvert(object)) {
                return converter.convert(object, configuration);
            }
        }

        return EvaluationValue.structureValue(new ReflectiveMapStructure(object));
    }
}
