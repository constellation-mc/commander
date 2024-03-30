package me.melontini.commander.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.command.selector.Extractor;
import me.melontini.commander.command.selector.Selector;
import me.melontini.commander.util.MagicCodecs;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@UtilityClass
public final class SelectorTypes {

    private static final BiMap<Identifier, Selector> SELECTORS = HashBiMap.create();
    private static final Map<Identifier, Map<String, Extractor>> EXTRACTORS = new HashMap<>();
    private static final Map<String, Extractor> DEFAULT = Collections.unmodifiableMap(Utilities.supply(new HashMap<>(), map -> {
        map.put("x", source -> String.valueOf(source.getPosition().x));
        map.put("y", source -> String.valueOf(source.getPosition().y));
        map.put("z", source -> String.valueOf(source.getPosition().z));
    }));

    public static final Codec<Selector> CODEC = MagicCodecs.mapLookup(SELECTORS);

    public static Selector getSelector(Identifier identifier) {
        return SELECTORS.get(identifier);
    }

    public static Extractor getExtractor(Identifier identifier, String parameter) {
        return EXTRACTORS.getOrDefault(identifier, DEFAULT).get(parameter);
    }

    public static Selector register(Identifier identifier, Selector selector, Consumer<Map<String, Extractor>> extractors) {
        var old = SELECTORS.put(identifier, selector);
        if (old != null) throw new IllegalStateException("Already registered selector %s".formatted(identifier));
        if (extractors != null) {
            var map = new HashMap<>(DEFAULT);
            extractors.accept(map);
            EXTRACTORS.put(identifier, Collections.unmodifiableMap(map));
        }
        return selector;
    }
}
