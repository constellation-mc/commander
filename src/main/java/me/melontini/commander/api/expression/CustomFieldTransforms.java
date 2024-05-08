package me.melontini.commander.api.expression;

import lombok.experimental.UtilityClass;
import me.melontini.commander.impl.expression.extensions.ReflectiveMapStructure;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Experimental
@UtilityClass
public class CustomFieldTransforms {

    public static <C> void register(Class<C> cls, String name, Function<C, Object> accessor) {
        ReflectiveMapStructure.addField(cls, name, accessor);
    }
}
