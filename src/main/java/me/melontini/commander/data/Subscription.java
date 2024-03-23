package me.melontini.commander.data;

import com.mojang.serialization.*;
import me.melontini.commander.command.ConditionedCommand;
import me.melontini.commander.data.types.EventTypes;
import me.melontini.commander.event.EventType;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record Subscription(EventType type, Object parameters, List<ConditionedCommand> list) {

    private static final Codec<List<ConditionedCommand>> LIST_CODEC = ExtraCodecs.list(ConditionedCommand.CODEC);
    public static final Codec<Subscription> CODEC = new MapCodec<Subscription>() {
        @Override
        public <T> RecordBuilder<T> encode(Subscription input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            var map = ops.mapBuilder();
            var r = EventTypes.CODEC.encodeStart(ops, input.type());
            map.add("event", r);
            if (!input.list().isEmpty()) map.add("commands", LIST_CODEC.encodeStart(ops, input.list()));
            if (input.parameters() != null) {
                var opt = input.type().context().get(EventType.EXTENSION);
                opt.ifPresent(codec -> map.add("parameters", codec.encodeStart(ops, Utilities.cast(input.parameters()))));
            }
            return map;
        }

        @Override
        public <T> DataResult<Subscription> decode(DynamicOps<T> ops, MapLike<T> input) {
            T type = input.get("event");
            if (type == null) return DataResult.error(() -> "Missing 'event' field in %s".formatted(input));
            DataResult<EventType> event = EventTypes.CODEC.parse(ops, type);

            T parameters = input.get("parameters");
            return event.flatMap(eventType -> {
                var opt = eventType.context().get(EventType.EXTENSION);
                if (opt.isPresent()) {
                    if (parameters == null)
                        return DataResult.error(() -> "Missing required 'parameters' field in %s".formatted(input));
                    return opt.get().parse(ops, parameters).map(Optional::ofNullable);
                }
                return DataResult.success(Optional.empty());
            }).map(o -> event.map(eventType -> {
                T commands = input.get("commands");
                if (commands != null)
                    return LIST_CODEC.parse(ops, commands).map(list1 -> new Subscription(eventType, o.orElse(null), list1));
                return DataResult.success(new Subscription(eventType, o.orElse(null), Collections.emptyList()));
            })).flatMap(Function.identity()).map(Function.identity()).flatMap(Function.identity()).map(Function.identity());
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("event", "parameters", "commands").map(ops::createString);
        }
    }.codec();
}
