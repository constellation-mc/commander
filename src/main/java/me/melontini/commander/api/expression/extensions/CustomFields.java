package me.melontini.commander.api.expression.extensions;

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import me.melontini.commander.impl.expression.extensions.ReflectiveMapStructure;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@UtilityClass
public class CustomFields {

  /**
   * Adds virtual fields to objects. This works only if the object is not handled by object converters. <br/>
   * For better UX, it's best to return types which are handled by converters.
   */
  public static <C> void addVirtualField(
      Class<C> cls, String name, BiFunction<C, LootContext, Object> accessor) {
    ReflectiveMapStructure.addField(cls, name, accessor);
  }

  public static <C> void addVirtualField(Class<C> cls, String name, Function<C, Object> accessor) {
    ReflectiveMapStructure.addField(cls, name, (c, lootContext) -> accessor.apply(c));
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
