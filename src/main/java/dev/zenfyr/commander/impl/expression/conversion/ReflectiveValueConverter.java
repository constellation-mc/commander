package dev.zenfyr.commander.impl.expression.conversion;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.*;
import com.google.common.collect.Lists;
import dev.zenfyr.commander.api.expression.extension.ProxyMap;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import dev.zenfyr.commander.impl.expression.conversion.gson.GsonConverter;
import dev.zenfyr.commander.impl.expression.conversion.misc.IdentifierConverter;
import dev.zenfyr.commander.impl.expression.conversion.misc.OptionalConverter;
import dev.zenfyr.commander.impl.expression.conversion.nbt.NbtConverter;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.List;
import java.util.Map;

public class ReflectiveValueConverter implements EvaluationValueConverterIfc {

  private static final List<ConverterIfc> converters = Lists.newArrayList(
      new NumberConverter(),
      new StringConverter(),
      new BooleanConverter(),
      new ExpressionNodeConverter(),
      new IdentifierConverter(),
      new NbtConverter(),
      new GsonConverter(),
      new ArrayConverter(),
      new OptionalConverter(),
      new DateTimeConverter(),
      new DurationConverter(),
      new StructureConverter(),
      new BinaryConverter());

  private final Map<Class<?>, ConverterIfc> fastPath = new Reference2ReferenceOpenHashMap<>();

  public static EvaluationValue convert(Object o) {
    return EvalUtils.CONFIGURATION
        .getEvaluationValueConverter()
        .convertObject(o, EvalUtils.CONFIGURATION);
  }

  @Override
  public EvaluationValue convertObject(Object object, ExpressionConfiguration configuration) {
    if (object == null) return EvaluationValue.NULL_VALUE;
    if (object instanceof EvaluationValue value) return value;

    // Proxy maps convert all of their outputs.
    if (object instanceof ProxyMap map)
      return EvaluationValue.structureValue((Map<String, EvaluationValue>) (Map<?, ?>) map);

    // fastpath things we already found
    var fast = this.fastPath.get(object.getClass());
    if (fast != null) return fast.convert(object, configuration);

    for (ConverterIfc converter : converters) {
      if (converter.canConvert(object)) {
        this.fastPath.put(object.getClass(), converter);
        return converter.convert(object, configuration);
      }
    }
    throw new IllegalStateException("Failed to fallback to binary converter?");
  }
}
