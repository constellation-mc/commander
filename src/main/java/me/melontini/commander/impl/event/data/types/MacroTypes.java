package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import me.melontini.commander.api.expression.MacroBuilder;
import me.melontini.commander.impl.util.macro.MacroContainer;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MacroTypes {

    private static final BiMap<Identifier, LootContextParameter<?>> KNOWN_PARAMETERS = HashBiMap.create();
    public static final Codec<LootContextParameter<?>> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, KNOWN_PARAMETERS);
    private static final Map<LootContextParameter<?>, MacroContainer> EXTRACTORS = new IdentityHashMap<>();

    private static final Supplier<MacroBuilder> DEFAULT = MacroBuilder::new;

    public static MacroContainer getMacros(LootContextParameter<?> id) {
        return EXTRACTORS.computeIfAbsent(id, id1 -> DEFAULT.get().build());
    }

    public static LootContextParameter<?> knowParameter(Identifier identifier) {
        return KNOWN_PARAMETERS.get(identifier);
    }

    public static void register(LootContextParameter<?> parameter, Consumer<MacroBuilder> extractors) {
        var old = KNOWN_PARAMETERS.put(parameter.getId(), parameter);
        if (old != null) throw new IllegalStateException("Already registered selector %s".formatted(parameter.getId()));
        if (extractors != null) {
            var builder = DEFAULT.get();
            extractors.accept(builder);
            EXTRACTORS.put(parameter, builder.build());
        }
    }
}
