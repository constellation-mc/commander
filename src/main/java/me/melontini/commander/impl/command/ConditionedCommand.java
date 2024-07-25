package me.melontini.commander.impl.command;

import com.mojang.serialization.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.impl.event.data.types.CommandTypes;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;

public record ConditionedCommand(Optional<LootCondition> condition, Command other)
    implements Command.Conditioned {

  public static final MapCodec<? extends Command.Conditioned> CODEC =
      new MapCodec<ConditionedCommand>() {
        @Override
        public <T> RecordBuilder<T> encode(
            ConditionedCommand input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
          var r = ((MapCodecCodec<Command>) CommandTypes.CODEC)
              .codec()
              .encode(input.other(), ops, prefix);
          input
              .condition()
              .map(condition1 -> LootConditionTypes.CODEC.encodeStart(ops, condition1))
              .ifPresent(tDataResult -> r.add("condition", tDataResult));
          return r;
        }

        @Override
        public <T> DataResult<ConditionedCommand> decode(DynamicOps<T> ops, MapLike<T> input) {
          var r = ((MapCodecCodec<Command>) CommandTypes.CODEC).codec().decode(ops, input);
          T condition = input.get("condition");
          if (condition == null)
            return r.map(command -> new ConditionedCommand(Optional.empty(), command));
          return r.map(command -> LootConditionTypes.CODEC
                  .parse(ops, condition)
                  .map(condition1 -> new ConditionedCommand(Optional.of(condition1), command)))
              .flatMap(Function.identity());
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
          return Stream.of("condition").map(ops::createString);
        }
      };

  @Override
  public boolean execute(EventContext context) {
    if (!this.condition
        .map(condition1 -> condition1.test(context.lootContext()))
        .orElse(true)) return false;
    return other().execute(context);
  }

  @Override
  public DataResult<Void> validate(EventType type) {
    return this.other().validate(type);
  }
}
