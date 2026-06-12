package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.TypeAdapter;
import com.ezylang.evalex.data.adapter.context.MethodAdapterContext;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.pulsar.util.functions.ThrowingSupplier;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ReflectiveTypeAdapter<T> implements TypeAdapter<T> {

  @FunctionalInterface
  private interface PropFn {
    EvaluationValue get(PropertyAdapterContext<?> context, EvaluationContext evalContext)
        throws EvaluationException;
  }

  @FunctionalInterface
  private interface PropFn1 extends PropFn {
    default EvaluationValue get(PropertyAdapterContext<?> context, EvaluationContext evalContext)
        throws EvaluationException {
      return this.get(context);
    }

    EvaluationValue get(PropertyAdapterContext<?> context) throws EvaluationException;
  }

  @FunctionalInterface
  private interface MethodFn {
    EvaluationValue invoke(MethodAdapterContext<?> context, EvaluationContext evalContext)
        throws EvaluationException;
  }

  @FunctionalInterface
  private interface MethodFn1 extends MethodFn {
    default EvaluationValue invoke(MethodAdapterContext<?> context, EvaluationContext evalContext)
        throws EvaluationException {
      return this.invoke(context);
    }

    EvaluationValue invoke(MethodAdapterContext<?> context) throws EvaluationException;
  }

  private static final PropFn UNDEFINED_PROP = (context, evalContext) -> EvaluationValue.UNDEFINED;
  private static final MethodFn UNDEFINED_METHOD =
      (context, evalContext) -> EvaluationValue.UNDEFINED;

  private final Map<String, ThrowingSupplier<PropFn, Throwable>> propFactories =
      new Object2ReferenceOpenHashMap<>();
  private final Map<String, ThrowingSupplier<MethodFn, Throwable>> methodFactories =
      new Object2ReferenceOpenHashMap<>();

  private final Map<String, PropFn> propCache = new Object2ReferenceOpenHashMap<>();
  private final Map<String, MethodFn> methodCache = new Object2ReferenceOpenHashMap<>();

  protected ReflectiveTypeAdapter() {
    scan(MethodHandles.lookup());
  }

  private void scan(MethodHandles.Lookup lookup) {
    for (Method m : getClass().getMethods()) {
      String name = m.getName();
      try {
        if (name.startsWith("prop_")) {
          propFactories.put(name.substring(5), () -> buildProp(lookup, m));
        } else if (name.startsWith("method_")) {
          methodFactories.put(name.substring(7), () -> buildMethod(lookup, m));
        }
      } catch (Throwable t) {
        throw new IllegalStateException("Failed to wire adapter method: " + name, t);
      }
    }

    methodFactories.put("_callable", () -> (context, evalContext) -> {
      List<EvaluationValue> result = new ArrayList<>();
      this.propFactories.keySet().stream()
          .filter(s -> !s.startsWith("_"))
          .map(EvaluationValue::stringValue)
          .forEach(result::add);
      this.methodFactories.keySet().stream()
          .filter(s -> !s.startsWith("_"))
          .map(s -> s + "(*)")
          .map(EvaluationValue::stringValue)
          .forEach(result::add);
      return EvaluationValue.arrayValue(result);
    });

    methodFactories.put("_wired", () -> (context, evalContext) -> {
      List<EvaluationValue> result = new ArrayList<>();
      this.propCache.keySet().stream()
          .filter(s -> !s.startsWith("_"))
          .map(EvaluationValue::stringValue)
          .forEach(result::add);
      this.methodCache.keySet().stream()
          .filter(s -> !s.startsWith("_"))
          .map(s -> s + "(*)")
          .map(EvaluationValue::stringValue)
          .forEach(result::add);
      return EvaluationValue.arrayValue(result);
    });
  }

  private PropFn buildProp(MethodHandles.Lookup lookup, Method m) throws Throwable {
    MethodHandle mh = lookup.unreflect(m);
    CallSite site;

    if (m.getParameterTypes().length == 1) {
      site = LambdaMetafactory.metafactory(
          lookup,
          "get",
          MethodType.methodType(PropFn1.class, getClass()),
          MethodType.methodType(EvaluationValue.class, PropertyAdapterContext.class),
          mh,
          MethodType.methodType(EvaluationValue.class, PropertyAdapterContext.class));
    } else if (m.getParameterTypes().length == 2) {
      site = LambdaMetafactory.metafactory(
          lookup,
          "get",
          MethodType.methodType(PropFn.class, getClass()),
          MethodType.methodType(
              EvaluationValue.class, PropertyAdapterContext.class, EvaluationContext.class),
          mh,
          MethodType.methodType(
              EvaluationValue.class, PropertyAdapterContext.class, EvaluationContext.class));
    } else {
      throw new AssertionError("Too many params for adapter method! Method %s: expected 1-2, got %s"
          .formatted(m.getName(), m.getParameterTypes().length));
    }

    return (PropFn) site.getTarget().invoke(this);
  }

  private MethodFn buildMethod(MethodHandles.Lookup lookup, Method m) throws Throwable {
    MethodHandle mh = lookup.unreflect(m);
    CallSite site;

    if (m.getParameterTypes().length == 1) {
      site = LambdaMetafactory.metafactory(
          lookup,
          "invoke",
          MethodType.methodType(MethodFn1.class, getClass()),
          MethodType.methodType(EvaluationValue.class, MethodAdapterContext.class),
          mh,
          MethodType.methodType(EvaluationValue.class, MethodAdapterContext.class));
    } else if (m.getParameterTypes().length == 2) {
      site = LambdaMetafactory.metafactory(
          lookup,
          "invoke",
          MethodType.methodType(MethodFn.class, getClass()),
          MethodType.methodType(
              EvaluationValue.class, MethodAdapterContext.class, EvaluationContext.class),
          mh,
          MethodType.methodType(
              EvaluationValue.class, MethodAdapterContext.class, EvaluationContext.class));
    } else {
      throw new AssertionError("Too many params for adapter method! Method %s: expected 1-2, got %s"
          .formatted(m.getName(), m.getParameterTypes().length));
    }

    return (MethodFn) site.getTarget().invoke(this);
  }

  @Override
  public EvaluationValue getProperty(
      PropertyAdapterContext<T> context, EvaluationContext evalContext) throws EvaluationException {
    PropFn fn = propCache.get(context.getName());
    if (fn == null) {
      var factory = propFactories.get(context.getName());
      if (factory == null) {
        this.propCache.put(context.getName(), UNDEFINED_PROP);
        return EvaluationValue.UNDEFINED;
      }

      try {
        fn = factory.get();
        this.propCache.put(context.getName(), fn);
      } catch (Throwable e) {
        throw new EvaluationException(context.getToken(), e.getMessage());
      }
    }
    return fn.get(context, evalContext);
  }

  @Override
  public EvaluationValue callFunction(
      MethodAdapterContext<T> context, EvaluationContext evalContext) throws EvaluationException {
    MethodFn fn = methodCache.get(context.getName());
    if (fn == null) {
      var factory = methodFactories.get(context.getName());
      if (factory == null) {
        this.methodCache.put(context.getName(), UNDEFINED_METHOD);
        return EvaluationValue.UNDEFINED;
      }

      try {
        fn = factory.get();
        this.methodCache.put(context.getName(), fn);
      } catch (Throwable e) {
        throw new EvaluationException(context.getToken(), e.getMessage());
      }
    }
    return fn.invoke(context, evalContext);
  }
}
