package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;

public class ExpressionLibraryAdapter implements TypeAdapter<ExpressionLibrary> {

  @Override
  public EvaluationValue getProperty(
      PropertyAdapterContext<ExpressionLibrary> ctx, EvaluationContext evalContext)
      throws EvaluationException {
    var func = ctx.getObject().getExpression(ctx.getName());
    if (func == null) return EvaluationValue.UNDEFINED;
    return ((Expression) func).solve(evalContext);
  }
}
