package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.event.EventType;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Set;

@UtilityClass
public final class EventTypes {

    private static final BiMap<Identifier, EventType> EVENTS = HashBiMap.create();
    public static final Codec<EventType> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, EVENTS);

    public static Identifier getId(EventType type) {
        return EVENTS.inverse().get(type);
    }

    public static EventType getType(Identifier identifier) {
        return EVENTS.get(identifier);
    }

    public static Set<EventType> types() {
        return Collections.unmodifiableSet(EVENTS.values());
    }

    public static EventType register(Identifier identifier, EventType type) {
        var old = EVENTS.put(identifier, type);
        if (old != null) throw new IllegalStateException("Already registered event %s".formatted(identifier));
        return type;
    }
}
