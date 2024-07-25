package me.melontini.commander.impl.expression.extensions.convert.components;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.extensions.CustomDataAccessor;
import net.minecraft.component.ComponentMap;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = false)
public class ComponentStruct implements CustomDataAccessor {

  private final ComponentMap map;

  public ComponentStruct(ComponentMap map) {
    this.map = map;
  }

  @Override
  public String toString() {
    return String.valueOf(map);
  }

  @Override
  public @Nullable Expression.Result getExpressionData(String variable, LootContext context)
      throws Exception {
    var component = context
        .getWorld()
        .getRegistryManager()
        .get(RegistryKeys.DATA_COMPONENT_TYPE)
        .get(Identifier.of(variable));
    if (component == null) return null;
    var result = map.get(component);
    if (result == null) return null;
    return Expression.Result.convert(result);
  }
}
