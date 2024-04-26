package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.command.Selector;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;

@UtilityClass
public final class SelectorTypes {

    private static final BiMap<Identifier, Selector> SELECTORS = HashBiMap.create();

    public static final Codec<Selector> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, SELECTORS);

    public static Selector register(Identifier identifier, Selector selector) {
        var old = SELECTORS.put(identifier, selector);
        if (old != null) throw new IllegalStateException("Already registered selector %s".formatted(identifier));
        return selector;
    }
}
