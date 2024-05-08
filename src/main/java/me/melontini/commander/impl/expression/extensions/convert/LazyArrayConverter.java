package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ArrayConverter;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.google.common.collect.Lists;
import me.melontini.commander.impl.expression.extensions.ProxyMap;

import java.util.List;

public class LazyArrayConverter implements ConverterIfc {

    private final ArrayConverter converter = new ArrayConverter();

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
        if (object instanceof List<?> l) return EvaluationValue.arrayValue(Lists.transform(l, ProxyMap::convert));
        return converter.convert(object, configuration);
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof List<?> || object.getClass().isArray();
    }
}
