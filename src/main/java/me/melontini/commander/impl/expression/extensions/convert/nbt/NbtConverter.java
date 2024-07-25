package me.melontini.commander.impl.expression.extensions.convert.nbt;

import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.conversion.NumberConverter;
import com.ezylang.evalex.data.types.ArrayValue;
import com.ezylang.evalex.data.types.StringValue;
import com.ezylang.evalex.data.types.StructureValue;
import com.ezylang.evalex.data.util.LazyListWrapper;
import java.util.Map;
import net.minecraft.nbt.*;

public class NbtConverter implements ConverterIfc {

  private final NumberConverter converter = new NumberConverter();

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {

    if (object instanceof AbstractNbtNumber n) {
      return converter.convert(n.numberValue(), configuration);
    } else if (object instanceof NbtString n) {
      return StringValue.of(n.asString());
    } else if (object instanceof AbstractNbtList<?> n) {
      return ArrayValue.of(new LazyListWrapper(n, configuration));
    } else if (object instanceof NbtCompound n) {
      return StructureValue.of((Map<String, EvaluationValue>) (Object) new NbtCompoundStruct(n));
    }

    throw illegalArgument(object);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof NbtElement;
  }
}
