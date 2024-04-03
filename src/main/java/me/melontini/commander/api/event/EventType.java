package me.melontini.commander.api.event;

import com.mojang.serialization.Codec;
import me.melontini.commander.impl.event.EventTypeImpl;
import me.melontini.dark_matter.api.base.util.Context;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

import static me.melontini.commander.impl.Commander.id;

public interface EventType extends Context {

    EventType NULL = EventType.builder().extension(null, subscriptions -> null).build(id("none"));

    Context.Key<Codec<?>> EXTENSION = Context.key("extension");
    Context.Key<Function<List<Subscription<?>>, ?>> FINALIZER = Context.key("finalizer");
    Context.Key<Codec<?>> CANCEL_TERM = Context.key("cancel_term");

    static EventType.Builder builder() {
        return new EventTypeImpl.Builder();
    }

    interface Builder {
        <T, C> Builder extension(Codec<T> extension, Function<List<Subscription<T>>, C> finalizer);
        <R> Builder cancelTerm(Codec<R> returnCodec);
        EventType build(Identifier identifier);
    }
}
