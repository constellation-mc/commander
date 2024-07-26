package me.melontini.commander.impl.expression.extensions.convert.gson;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.types.*;
import com.ezylang.evalex.data.util.LazyListWrapper;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;

public class GsonConverter implements ConverterIfc {

    @Override
    public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
        JsonElement element = (JsonElement) object;
        if (element.isJsonNull()) return NullValue.of();

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) return StringValue.of(primitive.getAsString());
            if (primitive.isBoolean()) return BooleanValue.of(primitive.getAsBoolean());
            if (primitive.isNumber()) return NumberValue.of(primitive.getAsBigDecimal());
            throw illegalArgument(object);
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            return ArrayValue.of(new LazyListWrapper(array.asList(), configuration));
        }

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            return StructureValue.of(Maps.transformValues(jsonObject.asMap(), ReflectiveValueConverter::convert));
        }

        throw illegalArgument(object);
    }

    @Override
    public boolean canConvert(Object object) {
        return object instanceof JsonElement;
    }
}
