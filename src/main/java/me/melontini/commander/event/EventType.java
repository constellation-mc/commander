package me.melontini.commander.event;

import com.mojang.serialization.Codec;
import me.melontini.commander.data.Subscription;
import me.melontini.commander.util.DataType;
import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public record EventType(Context context) {

    public static final Context.Key<Codec<?>> EXTENSION = Context.key("extension");
    public static final Context.Key<Function<List<Tuple<Identifier, Subscription>>, ?>> FINALIZER = Context.key("finalizer");
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

        public <T> Builder extension(Codec<T> extension) {
            builder.put(EXTENSION, extension);
            return this;
        }

        public <C> Builder finalizer(DataType<C> type, Function<List<Tuple<Identifier, Subscription>>, C> finalizer) {
            builder.put(FINALIZER, finalizer);
            return this;
        }

        public <R> Builder cancelTerm(Codec<R> returnCodec) {
            builder.put(CANCEL_TERM, returnCodec);
            return this;
        }

        public EventType build() {
            return new EventType(this.builder.build());
        }
    }
}
