package me.melontini.commander.impl.expression.extensions.convert.misc;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.conversion.ConverterIfc;
import com.ezylang.evalex.data.types.DataAccessorValue;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.api.expression.extensions.CustomDataAccessor;
import me.melontini.dark_matter.api.base.util.Exceptions;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

public class DataAccessorConverter implements ConverterIfc {

  @Override
  public EvaluationValue convert(Object object, ExpressionConfiguration configuration) {
    if (object instanceof DataAccessorIfc accessor)
      return DataAccessorValue.of(accessor); // Proxy maps convert all of their outputs.

    if (object instanceof CustomDataAccessor accessor)
      return DataAccessorValue.of(new AccessorWrapper(accessor));

    throw illegalArgument(object);
  }

  @Override
  public boolean canConvert(Object object) {
    return object instanceof DataAccessorIfc || object instanceof CustomDataAccessor;
  }

  public record AccessorWrapper(CustomDataAccessor accessor) implements DataAccessorIfc {

    @Override
    public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context) throws EvaluationException {
      try {
        return (EvaluationValue)
                accessor().getExpressionData(variable, (LootContext) context.context()[0]);
      } catch (Exception e) {
        throw new EvaluationException(token, Exceptions.unwrap(e).getMessage());
      }
    }
  }
}
