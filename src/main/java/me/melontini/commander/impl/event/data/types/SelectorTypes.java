package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.command.selector.MacroBuilder;
import me.melontini.commander.api.command.selector.Selector;
import me.melontini.commander.impl.util.macro.MacroContainer;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public final class SelectorTypes {

    private static final BiMap<Identifier, Selector> SELECTORS = HashBiMap.create();
    private static final Map<Identifier, MacroContainer> EXTRACTORS = new HashMap<>();
    private static final Supplier<MacroBuilder> DEFAULT = () -> new MacroBuilder()
            .arithmetic("x", source -> source.getPosition().x)
            .arithmetic("y", source -> source.getPosition().y)
            .arithmetic("z", source -> source.getPosition().z)
            .arithmetic("rot/x", source -> source.getRotation().x)
            .arithmetic("rot/y", source -> source.getRotation().y)
            .string("name", ServerCommandSource::getName)
            .string("world/key", source -> source.getWorld().getRegistryKey().getValue().toString());

    public static final Codec<Selector> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, SELECTORS);

    public static Selector getSelector(Identifier identifier) {
        return SELECTORS.get(identifier);
    }

    public static MacroContainer getMacros(Identifier id) {
        return EXTRACTORS.computeIfAbsent(id, id1 -> DEFAULT.get().build());
    }

    public static Selector register(Identifier identifier, Selector selector, Consumer<MacroBuilder> extractors) {
        var old = SELECTORS.put(identifier, selector);
        if (old != null) throw new IllegalStateException("Already registered selector %s".formatted(identifier));
        if (extractors != null) {
            var builder = DEFAULT.get();
            extractors.accept(builder);
            EXTRACTORS.put(identifier, builder.build());
        }
        return selector;
    }
}
