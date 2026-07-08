package dev.zenfyr.commander.impl.expression.adapter.core;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.IndexAdapterContext;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.ezylang.evalex.functions.string.StringFormatFunction;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.pulsar.api.util.Utilities;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class StringAdapter implements TypeAdapter<String> {

  private static final Map<String, StringFunction> METHODS =
      Utilities.supply(new Object2ReferenceOpenHashMap<>(), map -> {
        map.put("contains", new StringContains());
        map.put("endsWith", new StringEndsWith());
        map.put("startsWith", new StringStartsWith());
        map.put("matches", new StringMatches());

        map.put("upper", new StringUpper());
        map.put("lower", new StringLower());
        map.put("trim", new StringTrim());

        map.put("left", new StringLeft());
        map.put("right", new StringRight());
        map.put("substring", new StringSubstring());
        map.put("split", new StringSplit());
        map.put("format", new StringFormat());

        map.put("length", new StringLength());

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
  public EvaluationValue getIndex(
      IndexAdapterContext<String> context, EvaluationContext evalContext)
      throws EvaluationException {
    var _obj = context.getObject();
    EvaluationValue _index = context.getIndex().solve(evalContext);
    int i = _index.getNumberValue().intValue();

    if (i < 0 || i >= _obj.length()) {
      throw new EvaluationException(
          context.getToken(),
          String.format("Index %d out of bounds for string of length %d", i, _obj.length()));
    }

    return Expression.convertValue(_obj.charAt(i), evalContext);
  }

  @Override
  public EvaluationValue callFunction(
      MethodAdapterContext<String> context, EvaluationContext evalContext)
      throws EvaluationException {
    StringFunction func = METHODS.get(context.getName());
    if (func == null) return EvaluationValue.UNDEFINED;
    return func.call(context, evalContext);
  }

  private interface StringFunction {
    EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException;
  }

  private static final class StringContains implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      return EvaluationValue.booleanValue(
          context.getObject().contains(context.getArgs()[0].solve(evalContext).getStringValue()));
    }
  }

  private static final class StringEndsWith implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      return EvaluationValue.booleanValue(
          context.getObject().endsWith(context.getArgs()[0].solve(evalContext).getStringValue()));
    }
  }

  private static final class StringStartsWith implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      return EvaluationValue.booleanValue(
          context.getObject().startsWith(context.getArgs()[0].solve(evalContext).getStringValue()));
    }
  }

  private static final class StringMatches implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      String pattern = context.getArgs()[0].solve(evalContext).getStringValue();

      try {
        return EvaluationValue.booleanValue(context.getObject().matches(pattern));
      } catch (PatternSyntaxException e) {
        throw new EvaluationException(context.getToken(), e.getMessage(), e);
      }
    }
  }

  private static final class StringLower implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);
      return EvaluationValue.stringValue(context.getObject().toLowerCase());
    }
  }

  private static final class StringUpper implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);
      return EvaluationValue.stringValue(context.getObject().toUpperCase());
    }
  }

  private static final class StringTrim implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);
      return EvaluationValue.stringValue(context.getObject().trim());
    }
  }

  private static final class StringLeft implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _str = context.getObject();
      int length = Math.max(
          0,
          Math.min(
              context.getArgs()[0].solve(evalContext).getNumberValue().intValue(), _str.length()));
      return EvaluationValue.stringValue(_str.substring(0, length));
    }
  }

  private static final class StringRight implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _str = context.getObject();
      int length = Math.max(
          0,
          Math.min(
              context.getArgs()[0].solve(evalContext).getNumberValue().intValue(), _str.length()));
      return EvaluationValue.stringValue(_str.substring(_str.length() - length));
    }
  }

  private static final class StringSubstring implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 2);
      var _str = context.getObject();
      int start = context.getArgs()[0].solve(evalContext).getNumberValue().intValue();
      int end = context.getArgs()[1].solve(evalContext).getNumberValue().intValue();

      if (start < 0 || end < 0) {
        throw new EvaluationException(
            context.getToken(),
            "End '%s' and start '%s' indexes must be above 0".formatted(end, start));
      }

      if (end < start) {
        throw new EvaluationException(
            context.getToken(),
            "End index '%s' must be greater than or equal to start index '%s'"
                .formatted(end, start));
      }

      return EvaluationValue.stringValue(_str.substring(start, end));
    }
  }

  private static final class StringSplit implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 1);
      var _str = context.getObject();
      var separator = context.getArgs()[0].solve(evalContext).getStringValue();

      try {
        return Expression.convertValue(_str.split(separator), evalContext);
      } catch (PatternSyntaxException e) {
        Commander.LOGGER.error(String.valueOf(context.getToken()), e);
        throw new EvaluationException(context.getToken(), e.getMessage(), e);
      }
    }
  }

  private static final class StringFormat implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      var _str = context.getObject();

      // offset by 1 bc getFormatArguments expects the first arg to be the string (unused)
      EvaluationValue[] funcArgs = new EvaluationValue[context.getArgs().length + 1];
      for (int i = 0; i < context.getArgs().length; i++) {
        funcArgs[i + 1] = context.getArgs()[i].solve(evalContext);
      }

      Object[] arguments = StringFormatFunction.getFormatArguments(funcArgs, context.getConfig());

      try {
        return EvaluationValue.stringValue(String.format(_str, arguments));
      } catch (IllegalFormatException e) {
        Commander.LOGGER.error(String.valueOf(context.getToken()), e);
        throw new EvaluationException(context.getToken(), e.getMessage(), e);
      }
    }
  }

  private static final class StringLength implements StringFunction {

    @Override
    public EvaluationValue call(MethodAdapterContext<String> context, EvaluationContext evalContext)
        throws EvaluationException {
      TypeAdapter.validateArgCount(context, 0);
      return EvaluationValue.numberValue(context.getObject().length());
    }
  }
}
