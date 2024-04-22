package me.melontini.commander.impl.event;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.event.Subscription;
import me.melontini.commander.impl.event.data.types.EventTypes;
import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record EventTypeImpl(Context context) implements EventType {

    @Override
    public <T> Optional<T> get(Key<T> key) {
        return context.get(key);
    }

    @Override
    public void forEach(BiConsumer<Key<?>, Object> consumer) {
        context.forEach(consumer);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    public static class Builder implements EventType.Builder {
        private final Context.Builder builder = Context.builder();

        @Override
        public <T, C> Builder extension(@Nullable Codec<T> extension, Function<List<Subscription<T>>, C> finalizer) {
            if (extension != null) builder.put(EXTENSION, extension);
            builder.put(FINALIZER, Utilities.cast(finalizer));
            return this;
        }

        @Override
        public <R> Builder cancelTerm(Codec<R> returnCodec) {
            builder.put(CANCEL_TERM, returnCodec);
            return this;
        }

        @Override
        public EventTypeImpl build(Identifier identifier) {
            var type = new EventTypeImpl(this.builder.build());
            EventTypes.register(identifier, type);
            return type;
        }
    }
}
