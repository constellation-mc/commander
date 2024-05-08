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
            return EvaluationValue.arrayValue(new LazyArrayWrappers.IntArray(arr));
        } else if (array instanceof long[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.LongArray(arr));
        } else if (array instanceof double[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.DoubleArray(arr));
        } else if (array instanceof float[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.FloatArray(arr));
        } else if (array instanceof short[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.ShortArray(arr));
        } else if (array instanceof char[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.CharArray(arr));
        } else if (array instanceof byte[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.ByteArray(arr));
        } else if (array instanceof boolean[] arr) {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.BooleanArray(arr));
        } else {
            return EvaluationValue.arrayValue(new LazyArrayWrappers.ObjectArray((Object[]) array));
        }
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof List<?> || object.getClass().isArray();
    }
}
