package dev.zenfyr.commander.impl.expression.adapter.core;

import static dev.zenfyr.commander.impl.expression.EvalUtils.runLambda;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.google.common.collect.Lists;
import dev.zenfyr.pulsar.util.ExceptionUtil;
import dev.zenfyr.pulsar.util.Utilities;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAdapter extends com.ezylang.evalex.data.adapter.basic.ListAdapter {

  private static final Map<String, ListFunction> METHODS =
      Utilities.supply(new Object2ReferenceOpenHashMap<>(), map -> {
        map.put("allMatch", new AllMatchFunction());
        map.put("anyMatch", new AnyMatchFunction());
        map.put("noneMatch", new NoneMatchFunction());
        map.put("find", new FindFunction());
        map.put("findAny", new FindAnyFunction());
        map.put("findFirst", new FindFirstFunction());
        map.put("map", new MapFunction());

        map.put("_callable", (context, evalContext) -> {
          List<EvaluationValue> result = new ArrayList<>();
          map.keySet().stream()
              .filter(s -> !s.startsWith("_"))
              .map(s -> s + "(*)")
              .map(EvaluationValue::stringValue)
              .forEach(result::add);
          return EvaluationValue.arrayValue(result);
        });
      });

  @Override
  public EvaluationValue callFunction(
      MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
      throws EvaluationException {
    ListFunction func = METHODS.get(context.getName());
    if (func == null) return EvaluationValue.UNDEFINED;
    return func.call(context, evalContext);
  }

  private interface ListFunction {
    EvaluationValue call(MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException;
  }

  private static final class AllMatchFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext) {
      var _args = context.getArgs();

      return EvaluationValue.booleanValue(context.getObject().stream()
          .allMatch(value -> ExceptionUtil.supply(() ->
                  runLambda(evalContext, Expression.convertValue(value, evalContext), _args[0]))
              .getBooleanValue()));
    }
  }

  private static final class AnyMatchFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _args = context.getArgs();

      return EvaluationValue.booleanValue(context.getObject().stream()
          .anyMatch(value -> ExceptionUtil.supply(() ->
                  runLambda(evalContext, Expression.convertValue(value, evalContext), _args[0]))
              .getBooleanValue()));
    }
  }

  private static final class NoneMatchFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _args = context.getArgs();

      return EvaluationValue.booleanValue(context.getObject().stream()
          .noneMatch(value -> ExceptionUtil.supply(() ->
                  runLambda(evalContext, Expression.convertValue(value, evalContext), _args[0]))
              .getBooleanValue()));
    }
  }

  private static final class FindFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _args = context.getArgs();

      return EvaluationValue.arrayValue(context.getObject().stream()
          .map(o -> Expression.convertValue(o, evalContext))
          .filter(value -> ExceptionUtil.supply(() -> runLambda(evalContext, value, _args[0]))
              .getBooleanValue())
          .toList());
    }
  }

  private static final class FindAnyFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);

      return context.getObject().stream()
          .findAny()
          .map(o -> Expression.convertValue(o, evalContext))
          .orElse(EvaluationValue.NULL_VALUE);
    }
  }

  private static final class FindFirstFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);

      return context.getObject().stream()
          .findFirst()
          .map(o -> Expression.convertValue(o, evalContext))
          .orElse(EvaluationValue.NULL_VALUE);
    }
  }

  private static final class MapFunction implements ListFunction {

    @Override
    public EvaluationValue call(
        MethodAdapterContext<List<?>> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _args = context.getArgs();

      return EvaluationValue.arrayValue(Lists.transform(
          context.getObject(),
          input -> ExceptionUtil.supply(() ->
              runLambda(evalContext, Expression.convertValue(input, evalContext), _args[0]))));
    }
  }
}
