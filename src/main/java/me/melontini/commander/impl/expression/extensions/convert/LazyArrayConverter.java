package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.google.common.collect.Lists;
import me.melontini.commander.impl.expression.extensions.ProxyMap;

import java.util.List;

public class LazyArrayConverter implements ConverterIfc {

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
        if (object instanceof List<?> l) return EvaluationValue.arrayValue(Lists.transform(l, ProxyMap::convert));
        return convertArray(object);
    }

    private EvaluationValue convertArray(Object array) {
        if (array instanceof int[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof long[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof double[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof float[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof short[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof char[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof byte[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else if (array instanceof boolean[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length));
        } else {
            return EvaluationValue.arrayValue(new LazyArrayWrapper(value -> ((Object[]) array)[value], ((Object[]) array).length));
        }
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof List<?> || object.getClass().isArray();
    }
}
