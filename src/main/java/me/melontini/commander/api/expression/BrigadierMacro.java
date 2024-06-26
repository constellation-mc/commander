package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.expression.macro.PatternParser;
import net.minecraft.loot.context.LootContext;

import java.util.function.Function;

/**
 * A special type of string function with support for {@code ${{}}} macros.
 * <p>The main purpose is to enable command macros in {@code commander:commands}, but can be used anywhere else.</p>
 */
public interface BrigadierMacro extends Function<LootContext, String> {

    Codec<BrigadierMacro> CODEC = Codec.STRING.comapFlatMap(BrigadierMacro::parse, BrigadierMacro::original);

    static DataResult<BrigadierMacro> parse(String input) {
        return PatternParser.parse(input);
    }

    @Override
    default String apply(LootContext context) {
        return this.build(context);
    }

    String build(LootContext context);
    String original();
}
