package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.function.Function;
import me.melontini.commander.impl.expression.macro.PatternParser;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A special type of string function with support for {@code ${{}}} macros.
 * <p>The main purpose is to enable command macros in {@code commander:commands}, but can be used anywhere else.</p>
 */
@ApiStatus.NonExtendable
public interface BrigadierMacro extends Function<LootContext, String> {

  Codec<BrigadierMacro> CODEC =
      Codec.STRING.comapFlatMap(BrigadierMacro::parse, BrigadierMacro::original);

  static DataResult<BrigadierMacro> parse(String input) {
    return PatternParser.parse(input);
  }

  /**
   * @return The command string with expression results inserted as strings.
   * @see Expression#eval(LootContext)
   */
  default String build(LootContext context) {
    return this.build(context, null);
  }

  /**
   * @see #build(LootContext)
   * @see Expression#eval(LootContext, Map)
   */
  @ApiStatus.Experimental
  String build(LootContext context, @Nullable Map<String, Object> params);

  /**
   * @return The original input string.
   */
  String original();

  @Override
  default String apply(LootContext context) {
    return build(context);
  }
}
