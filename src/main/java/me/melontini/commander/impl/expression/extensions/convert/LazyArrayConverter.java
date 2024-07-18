package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.google.common.collect.Lists;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;

import java.util.Collections;
import java.util.List;

public class LazyArrayConverter implements ConverterIfc {

    public static final EvaluationValue EMPTY = EvaluationValue.arrayValue(Collections.emptyList());

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
        if (object instanceof List<?> l) return !l.isEmpty() ? EvaluationValue.arrayValue(Lists.transform(l, ReflectiveValueConverter::convert)) : EMPTY;
        return convertArray(object);
    }

    private EvaluationValue convertArray(Object array) {
        if (array instanceof int[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof long[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof double[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof float[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof short[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof char[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof byte[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else if (array instanceof boolean[] arr) {
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        } else {
            var arr = ((Object[]) array);
            return arr.length > 0 ? EvaluationValue.arrayValue(new LazyArrayWrapper(value -> arr[value], arr.length)) : EMPTY;
        }
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof List<?> || object.getClass().isArray();
    }
}
