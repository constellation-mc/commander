package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import me.melontini.commander.api.expression.ExtractionBuilder;
import me.melontini.commander.impl.util.macro.ExtractionContainer;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExtractionTypes {

    private static final BiMap<Identifier, LootContextParameter<?>> KNOWN_PARAMETERS = HashBiMap.create();
    public static final Codec<LootContextParameter<?>> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, KNOWN_PARAMETERS);
    private static final Map<LootContextParameter<?>, ExtractionContainer> EXTRACTION = new IdentityHashMap<>();

    private static final Supplier<ExtractionBuilder> DEFAULT = ExtractionBuilder::new;

    public static ExtractionContainer getMacros(LootContextParameter<?> id) {
        return EXTRACTION.computeIfAbsent(id, id1 -> DEFAULT.get().build());
    }

    public static LootContextParameter<?> knownParameter(Identifier identifier) {
        return KNOWN_PARAMETERS.get(identifier);
    }

    public static void register(LootContextParameter<?> parameter, Consumer<ExtractionBuilder> extractors) {
        var old = KNOWN_PARAMETERS.put(parameter.getId(), parameter);
        if (old != null) throw new IllegalStateException("Already registered extraction %s".formatted(parameter.getId()));
        if (extractors != null) {
            var builder = DEFAULT.get();
            extractors.accept(builder);
            EXTRACTION.put(parameter, builder.build());
        }
    }
}
