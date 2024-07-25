package me.melontini.commander.impl.expression.extensions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.*;
import com.ezylang.evalex.data.types.DataAccessorValue;
import com.ezylang.evalex.data.types.NullValue;
import com.ezylang.evalex.data.types.StructureValue;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import me.melontini.commander.api.expression.extensions.CustomDataAccessor;
import me.melontini.commander.api.expression.extensions.ObjectConverter;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.convert.nbt.NbtConverter;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.MathUtil;
import net.minecraft.loot.context.LootContext;

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
    return EvalUtils.CONFIGURATION
        .getEvaluationValueConverter()
        .convertObject(o, EvalUtils.CONFIGURATION);
  }

  @Override
  public EvaluationValue convertObject(Object object, ExpressionConfiguration configuration) {
    if (object == null) return NullValue.of();
    if (object instanceof EvaluationValue value) return value;

    if (object instanceof DataAccessorIfc accessor)
      return DataAccessorValue.of(accessor); // Proxy maps convert all of their outputs.

    if (object instanceof CustomDataAccessor accessor)
      return DataAccessorValue.of((variable, token, context) -> {
        try {
          return (EvaluationValue)
              accessor.getExpressionData(variable, (LootContext) context.context()[0]);
        } catch (Exception e) {
          throw new EvaluationException(token, Exceptions.unwrap(e).getMessage());
        }
      });

    if (object instanceof ProxyMap map)
      return StructureValue.of((Map<String, EvaluationValue>) (Object) map);

    for (ConverterIfc converter : converters) {
      if (converter.canConvert(object)) return converter.convert(object, configuration);
    }

    return DataAccessorValue.of(new ReflectiveMapStructure(object));
  }
}
