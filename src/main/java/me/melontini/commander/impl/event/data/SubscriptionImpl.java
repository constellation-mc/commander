package me.melontini.commander.impl.event.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.event.Subscription;
import me.melontini.commander.impl.event.data.types.EventTypes;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record SubscriptionImpl<E>(EventType type, @Nullable E parameters, List<Command.Conditioned> list) implements Subscription<E> {

    private static final Codec<List<Command.Conditioned>> LIST_CODEC = ExtraCodecs.list(Command.CODEC);
    public static final MapCodec<? extends Subscription<?>> BASE_CODEC = new MapCodec<SubscriptionImpl<?>>() {
        @Override
        public <T> RecordBuilder<T> encode(SubscriptionImpl<?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            var map = ops.mapBuilder();
            var r = EventTypes.CODEC.encodeStart(ops, input.type());
            map.add("event", r);
            if (!input.list().isEmpty()) map.add("commands", LIST_CODEC.encodeStart(ops, input.list()));
            if (input.parameters() != null) {
                var opt = input.type().get(EventType.EXTENSION);
                opt.ifPresent(codec -> map.add("parameters", codec.encodeStart(ops, Utilities.cast(input.parameters()))));
            }
            return map;
        }

        @Override
        public <T> DataResult<SubscriptionImpl<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            T type = input.get("event");
            if (type == null) return DataResult.error(() -> "Missing 'event' field in %s".formatted(input));
            DataResult<EventType> event = EventTypes.CODEC.parse(ops, type);

            T parameters = input.get("parameters");
            return event.flatMap(eventType -> {
                var opt = eventType.get(EventType.EXTENSION);
                if (opt.isPresent()) {
                    if (parameters == null)
                        return DataResult.error(() -> "Missing required 'parameters' field in %s".formatted(input));
                    return opt.get().parse(ops, parameters).map(Optional::ofNullable);
                }
                return DataResult.success(Optional.empty());
            }).map(o -> event.map(eventType -> {
                T commands = input.get("commands");
                if (commands != null)
                    return LIST_CODEC.parse(ops, commands).flatMap(list1 -> {
                        for (Command.Conditioned conditionedCommand : list1) {
                            var r = conditionedCommand.validate(eventType);
                            if (r.error().isPresent())
                                return r.map(unused -> Collections.<Command.Conditioned>emptyList());
                        }
                        return DataResult.success(list1);
                    }).map(list1 -> new SubscriptionImpl<>(eventType, o.orElse(null), (List<Command.Conditioned>) list1));
                return DataResult.success(new SubscriptionImpl<>(eventType, o.orElse(null), Collections.emptyList()));
            })).flatMap(Function.identity()).map(Function.identity()).flatMap(r -> r).map(Function.identity());
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("event", "parameters", "commands").map(ops::createString);
        }
    };

    public static final Codec<List<? extends Subscription<?>>> CODEC = ExtraCodecs.either(ExtraCodecs.list(BASE_CODEC.codec()).fieldOf("events").codec(), BASE_CODEC.codec())
            .xmap(e -> e.map(Function.identity(), Collections::singletonList), subscriptions -> Utilities.cast(Either.left(subscriptions)));
}
