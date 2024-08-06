package me.melontini.commander.impl.util.loot;

import java.util.Optional;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;

public class LootUtil {

  public static LootContext build(LootContextParameterSet parameters) {
    return new LootContext.Builder(parameters).build(Optional.empty());
  }
}
