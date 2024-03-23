package me.melontini.commander.command;

import com.mojang.serialization.*;
import me.melontini.commander.data.types.CommandTypes;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.util.MagicCodecs;
import net.minecraft.loot.condition.LootCondition;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record ConditionedCommand(Optional<LootCondition> condition, Command other) {

    public static final Codec<ConditionedCommand> CODEC = new MapCodec<ConditionedCommand>() {
        @Override
        public <T> RecordBuilder<T> encode(ConditionedCommand input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            var r = ((MapCodecCodec<Command>) CommandTypes.CODEC).codec().encode(input.other(), ops, prefix);
            input.condition().map(condition1 -> MagicCodecs.LOOT_CONDITION.encodeStart(ops, condition1)).ifPresent(tDataResult -> r.add("condition", tDataResult));
            return r;
        }

        @Override
        public <T> DataResult<ConditionedCommand> decode(DynamicOps<T> ops, MapLike<T> input) {
            var r = ((MapCodecCodec<Command>) CommandTypes.CODEC).codec().decode(ops, input);
            T condition = input.get("condition");
            if (condition == null) return r.map(command -> new ConditionedCommand(Optional.empty(), command));
            return r.map(command -> MagicCodecs.LOOT_CONDITION.parse(ops, condition).map(condition1 -> new ConditionedCommand(Optional.of(condition1), command))).flatMap(Function.identity());
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("condition").map(ops::createString);
        }
    }.codec();

    public boolean execute(EventContext context) {
        if (!this.condition.map(condition1 -> condition1.test(context.lootContext())).orElse(true)) return false;
        return other().execute(context);
    }
}
