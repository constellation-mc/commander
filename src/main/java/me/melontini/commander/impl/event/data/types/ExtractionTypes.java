package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ExtractionTypes {

    private static final BiMap<Identifier, LootContextParameter<?>> KNOWN_PARAMETERS = HashBiMap.create();
    public static final Codec<LootContextParameter<?>> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, KNOWN_PARAMETERS);

    public static LootContextParameter<?> getParameter(Identifier identifier) {
        return Objects.requireNonNull(KNOWN_PARAMETERS.get(identifier), () -> "No such loot context parameter %s!".formatted(identifier));
    }

    public static void register(LootContextParameter<?> parameter) {
        var old = KNOWN_PARAMETERS.put(parameter.getId(), parameter);
        if (old != null) throw new IllegalStateException("Already registered context %s".formatted(parameter.getId()));
    }
}
