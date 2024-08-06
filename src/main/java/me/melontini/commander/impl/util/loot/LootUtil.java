package me.melontini.commander.impl.util.loot;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;

public class LootUtil {

  public static LootContext build(LootContextParameterSet parameters) {
    return new LootContext.Builder(parameters).build(null);
  }
}
