package dev.zenfyr.commander.impl.expression.conversion.gson;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.util.LazyListWrapper;
import com.ezylang.evalex.data.util.LazyMapWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GsonConverter implements ConverterIfc {

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    JsonElement element = (JsonElement) object;
    if (element.isJsonNull()) return EvaluationValue.NULL_VALUE;

    if (element.isJsonPrimitive()) {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (primitive.isString()) return EvaluationValue.stringValue(primitive.getAsString());
      if (primitive.isBoolean()) return EvaluationValue.booleanValue(primitive.getAsBoolean());
      if (primitive.isNumber())
        return Expression.convertValue(primitive.getAsBigDecimal(), configuration);
      throw illegalArgument(object);
    }

    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      return EvaluationValue.arrayValue(new LazyListWrapper(array.asList(), configuration));
    }

    if (element.isJsonObject()) {
      JsonObject jsonObject = element.getAsJsonObject();
      return EvaluationValue.structureValue(new LazyMapWrapper(jsonObject.asMap(), configuration));
    }

    throw illegalArgument(object);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof JsonElement;
  }
}
