package me.melontini.commander.impl.event.data.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;
import me.melontini.commander.api.event.EventType;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.UnmodifiableView;

@UtilityClass
public final class EventTypes {

  private static final BiMap<Identifier, EventType> EVENTS = HashBiMap.create();
  public static final Codec<EventType> CODEC = ExtraCodecs.mapLookup(Identifier.CODEC, EVENTS);

  public static Identifier getId(EventType type) {
    return Objects.requireNonNull(
        EVENTS.inverse().get(type), () -> "Unregistered EventType %s!".formatted(type));
  }

  public static EventType getType(Identifier identifier) {
    return Objects.requireNonNull(
        EVENTS.get(identifier), "Unknown EventType %s!".formatted(identifier));
  }

  public static @UnmodifiableView Set<EventType> types() {
    return Collections.unmodifiableSet(EVENTS.values());
  }

  public static EventType register(Identifier identifier, EventType type) {
    var old = EVENTS.put(identifier, type);
    if (old != null)
      throw new IllegalStateException("Already registered event %s".formatted(identifier));
    return type;
  }
}
