package me.melontini.commander.api.expression.extensions;

import java.util.function.Function;
import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * An object converter converts objects to types supported by expressions.
 */
@ApiStatus.OverrideOnly
@ApiStatus.Experimental
public interface ObjectConverter {

  static ObjectConverter ofClasses(
      Function<Object, Expression.Result> function, Class<?>... classes) {
    return new ObjectConverter() {
      @Override
      public Expression.@NotNull Result convert(@NotNull Object object) {
        return function.apply(object);
      }

      @Override
      public boolean canConvert(@NotNull Object object) {
        for (Class<?> aClass : classes) {
          if (aClass.isInstance(object)) return true;
        }
        return false;
      }
    };
  }

  static <C> ObjectConverter ofClass(Class<C> cls, Function<C, Expression.Result> function) {
    return new ObjectConverter() {
      @Override
      public Expression.@NotNull Result convert(@NotNull Object object) {
        return function.apply((C) object);
      }

      @Override
      public boolean canConvert(@NotNull Object object) {
        return cls.isInstance(object);
      }
    };
  }

  Expression.@NotNull Result convert(@NotNull Object object);

  /**
   * You must be absolutely sure that this object is convertible.
   * Do not return {@code null} in {@link #convert(Object)}.
   * @param object Non-null object of unspecified type.
   * @return Whenever the object can be converted by the converted.
   */
  boolean canConvert(@NotNull Object object);

  default IllegalArgumentException illegalArgument(Object object) {
    return new IllegalArgumentException(
        "Unsupported data type '" + object.getClass().getName() + "'");
  }
}
