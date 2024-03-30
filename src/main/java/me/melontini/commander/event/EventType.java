package me.melontini.commander.event;

import com.mojang.serialization.Codec;
import me.melontini.commander.data.Subscription;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.util.DataType;
import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

import static me.melontini.commander.Commander.id;

public record EventType(Context context) {

    public static final Context.Key<Codec<?>> EXTENSION = Context.key("extension");
    public static final Context.Key<Function<List<Subscription<?>>, ?>> FINALIZER = Context.key("finalizer");
    public static final Context.Key<Codec<?>> CANCEL_TERM = Context.key("cancel_term");

    public static final EventType NULL = EventType.builder().extension(null, subscriptions -> null).build(id("none"));

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Context.Builder builder = Context.builder();

        public <T, C> Builder extension(Codec<T> extension, Function<List<Subscription<T>>, C> finalizer) {
            return extension(extension, finalizer, null);
        }

        public <T, C> Builder extension(Codec<T> extension, Function<List<Subscription<T>>, C> finalizer, DataType<C> type) {
            if (extension != null) builder.put(EXTENSION, extension);
            builder.put(FINALIZER, Utilities.cast(finalizer));
            return this;
        }

        public <R> Builder cancelTerm(Codec<R> returnCodec) {
            builder.put(CANCEL_TERM, returnCodec);
            return this;
        }

        public EventType build(Identifier identifier) {
            return EventTypes.register(identifier, new EventType(this.builder.build()));
        }
    }
}
