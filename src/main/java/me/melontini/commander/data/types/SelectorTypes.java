package me.melontini.commander.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.command.selector.Selector;
import me.melontini.commander.util.MagicCodecs;
import net.minecraft.util.Identifier;

@UtilityClass
public final class SelectorTypes {

    private static final BiMap<Identifier, Selector> SELECTORS = HashBiMap.create();
    public static final Codec<Selector> CODEC = MagicCodecs.mapLookup(SELECTORS);

    public static Selector register(Identifier identifier, Selector selector) {
        var old = SELECTORS.put(identifier, selector);
        if (old != null) throw new IllegalStateException("Already registered selector %s".formatted(identifier));
        return selector;
    }
}
