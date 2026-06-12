package dev.zenfyr.commander.impl.expression.conversion.nbt;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.conversion.NumberConverter;
import com.ezylang.evalex.data.util.LazyListWrapper;
import com.ezylang.evalex.data.util.LazyMapWrapper;
import dev.zenfyr.commander.impl.mixin.CompoundTagAccessor;
import net.minecraft.nbt.*;

public class NbtConverter implements ConverterIfc {

  private final NumberConverter converter = new NumberConverter();

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {

    if (object instanceof NumericTag n) {
      return converter.convert(n.getAsNumber(), configuration);
    } else if (object instanceof StringTag n) {
      return EvaluationValue.stringValue(n.getAsString());
    } else if (object instanceof CollectionTag<?> n) {
      return EvaluationValue.arrayValue(new LazyListWrapper(n, configuration));
    } else if (object instanceof CompoundTag n) {
      return EvaluationValue.structureValue(
          new LazyMapWrapper(((CompoundTagAccessor) n).commander$toMap(), configuration));
    }

    throw illegalArgument(object);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof Tag;
  }
}
