package me.melontini.commander.api.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.melontini.commander.impl.util.macro.PatternParser;
import net.minecraft.loot.context.LootContext;

public interface BrigadierMacro {

    Codec<BrigadierMacro> CODEC = Codec.STRING.comapFlatMap(BrigadierMacro::parse, BrigadierMacro::original);

    static DataResult<BrigadierMacro> parse(String input) {
        return PatternParser.parse(input);
    }

    String build(LootContext context);
    String original();
}
