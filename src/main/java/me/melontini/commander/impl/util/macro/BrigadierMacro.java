package me.melontini.commander.impl.util.macro;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.event.EventContext;

public interface BrigadierMacro {

    Codec<BrigadierMacro> CODEC = Codec.STRING.comapFlatMap(PatternParser::parse, BrigadierMacro::original);

    String build(EventContext context);
    String original();
}
