package me.melontini.commander.event;

import com.mojang.serialization.Codec;
import me.melontini.commander.data.Subscription;
import me.melontini.dark_matter.api.base.util.Context;

import java.util.function.Function;
import java.util.stream.Stream;

public record EventType(Context context) {

    public static final Context.Key<Codec<?>> EXTENSION = Context.key("extension");
    public static final Context.Key<Function<Stream<Subscription>, ?>> FINALIZER = Context.key("finalizer");
    public static final Context.Key<Codec<?>> CANCEL_TERM = Context.key("cancel_term");

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Context.Builder builder = Context.builder();

        public Builder extension(Codec<?> extension, Function<Stream<Subscription>, ?> finalizer) {
            builder.put(EXTENSION, extension);
            builder.put(FINALIZER, finalizer);
            return this;
        }

        public Builder cancelTerm(Codec<?> returnCodec) {
            builder.put(CANCEL_TERM, returnCodec);
            return this;
        }

        public EventType build() {
            return new EventType(this.builder.build());
        }
    }
}
