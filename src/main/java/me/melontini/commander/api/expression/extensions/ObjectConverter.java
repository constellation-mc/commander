package me.melontini.commander.api.expression.extensions;

import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Experimental
public interface ObjectConverter {

    static ObjectConverter ofClasses(Function<Object, Expression.Result> function, Class<?>... classes) {
        return new ObjectConverter() {
            @Override
            public Expression.Result convert(Object object) {
                return function.apply(object);
            }

            @Override
            public boolean canConvert(Object object) {
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
            public Expression.Result convert(Object object) {
                return function.apply((C) object);
            }

            @Override
            public boolean canConvert(Object object) {
                return cls.isInstance(object);
            }
        };
    }

    Expression.Result convert(Object object);
    boolean canConvert(Object object);

    default IllegalArgumentException illegalArgument(Object object) {
        return new IllegalArgumentException(
                "Unsupported data type '" + object.getClass().getName() + "'");
    }
}
