package me.melontini.commander.impl.expression.macro;

import java.util.Map;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.BrigadierMacro;
import net.minecraft.loot.context.LootContext;

@Log4j2
public record DynamicMacro(String original, PatternParser.Appender start)
    implements BrigadierMacro {

  @Override
  public String build(LootContext context, Map<String, Object> params) {
    return start.build(context, params, new StringBuilder()).toString();
  }
}
