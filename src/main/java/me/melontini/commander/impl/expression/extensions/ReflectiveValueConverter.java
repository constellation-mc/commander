package me.melontini.commander.impl.expression.extensions;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.*;
import com.google.common.collect.Lists;
import me.melontini.commander.api.expression.extensions.ObjectConverter;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.convert.nbt.NbtConverter;
import me.melontini.dark_matter.api.base.util.MathUtil;

import java.util.List;

public class ReflectiveValueConverter implements EvaluationValueConverterIfc {

    private static final List<ConverterIfc> converters = Lists.newArrayList(
            new NumberConverter(),
            new StringConverter(),
            new BooleanConverter(),
            new ExpressionNodeConverter(),
            new NbtConverter(),
            new ArrayConverter(),
            new DateTimeConverter(),
            new DurationConverter());

    public static void registerConverter(int priority, ObjectConverter converter) {
        priority = MathUtil.clamp(priority, 0, converters.size());

        converters.add(priority, new ConverterIfc() {
            @Override
            public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
                return (EvaluationValue) (Object) converter.convert(object);
            }

            @Override
            public boolean canConvert(Object object) {
                return converter.canConvert(object);
            }
        });
    }

    public static EvaluationValue convert(Object o) {
        return EvalUtils.CONFIGURATION.getEvaluationValueConverter().convertObject(o, EvalUtils.CONFIGURATION);
    }

    @Override
    public EvaluationValue convertObject(Object object, ExpressionConfiguration configuration) {
        if (object == null) return EvaluationValue.NULL_VALUE;
        if (object instanceof EvaluationValue value) return value;
        if (object instanceof ProxyMap map) return EvaluationValue.structureValue(map); //Proxy maps convert all of their outputs.

        for (ConverterIfc converter : converters) {
            if (converter.canConvert(object)) return converter.convert(object, configuration);
        }

        return EvaluationValue.structureValue(new ReflectiveMapStructure(object));
    }
}
