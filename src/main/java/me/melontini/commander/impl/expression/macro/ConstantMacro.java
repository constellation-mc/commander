package me.melontini.commander.impl.expression.macro;

import java.util.Map;
import me.melontini.commander.api.expression.BrigadierMacro;
import net.minecraft.loot.context.LootContext;

public record ConstantMacro(String original) implements BrigadierMacro {
  @Override
  public String build(LootContext context, Map<String, Object> params) {
    return original();
  }
}
