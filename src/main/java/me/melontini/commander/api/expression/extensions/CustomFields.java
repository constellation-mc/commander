package me.melontini.commander.api.expression.extensions;

import lombok.experimental.UtilityClass;
import me.melontini.commander.impl.expression.extensions.ReflectiveMapStructure;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Experimental
@UtilityClass
public class CustomFields {

    /**
     * Allows adding virtual fields to objects. This works only if the object is not handled by object converters. <br/>
     * For better UX, it's best to return types which are handled by converters.
     */
    public static <C> void addVirtualField(Class<C> cls, String name, Function<C, Object> accessor) {
        ReflectiveMapStructure.addField(cls, name, accessor);
    }

    /**
     * Registers an {@link ObjectConverter} to convert objects to types supported by expressions. <br/>
     * This is useful for wrappers and complex map-like objects.
     */
    public static void registerConverter(ObjectConverter converter) {
        ReflectiveValueConverter.registerConverter(Integer.MAX_VALUE, converter);
    }

    /**
     * @see CustomFields#registerConverter(ObjectConverter)
     */
    public static void registerConverter(int priority, ObjectConverter converter) {
        ReflectiveValueConverter.registerConverter(priority, converter);
    }
}
