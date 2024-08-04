package me.melontini.commander.api.event;

import static me.melontini.commander.impl.Commander.id;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Function;
import me.melontini.commander.impl.event.EventTypeImpl;
import me.melontini.dark_matter.api.base.util.Context;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Event types define basic information about events, like the return type, additional parameters and the finalizer.
 *
 * <p>- {@link EventType#EXTENSION} allows you to pass additional parameters to the subscription and {@link EventType#FINALIZER} allows you to process those parameters.</p>
 * <p>- {@link EventType#CANCEL_TERM} defines if the {@code commander:cancel} command is supported by this event.</p>
 */
public interface EventType extends Context {

  /**
   * Dummy event type. Can be used to execute commands outside of events, or to replace subscriptions in JSON.
   */
  EventType NULL = EventType.builder().extension(null, subscriptions -> null).build(id("none"));

  Context.Key<Codec<?>> EXTENSION = Context.key("extension");
  Context.Key<Function<List<Subscription<?>>, ?>> FINALIZER = Context.key("finalizer");
  Context.Key<Codec<?>> CANCEL_TERM = Context.key("cancel_term");

  static EventType.Builder builder() {
    return new EventTypeImpl.Builder();
  }

  interface Builder {
    /**
     * Adds parameters to event declarations. Prefer using a {@link MapCodec} to avoid conflicts in the future.
     * @param extension The codec to decode parameters from JSON.
     * @param finalizer The function to process event {@link Subscription}s with parameters.
     */
    @Contract("_, _ -> this")
    <T, C> Builder extension(
        @Nullable Codec<T> extension, Function<List<Subscription<T>>, C> finalizer);

    /**
     * Adds a "cancel term" to the event.
     * This allows invoking the {@code commander:cancel} command from JSON to modify the return type.
     * @param returnCodec The codec to decode the object from JSON.
     */
    @Contract("_ -> this")
    <R> Builder cancelTerm(Codec<R> returnCodec);

    /**
     * Builds and registers the {@link EventType}.
     * @param identifier The event type identifier.
     * @return Newly constructed {@link EventType} instance.
     */
    EventType build(Identifier identifier);
  }
}
