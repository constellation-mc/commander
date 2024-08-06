package me.melontini.commander.impl.command;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import me.melontini.commander.impl.util.MagicCodecs;
import me.melontini.commander.impl.util.loot.LootUtil;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.ServerCommandSource;

public record ConditionedSelector(Optional<LootCondition> condition, Selector other)
    implements Selector.Conditioned {

  public static final Codec<? extends Selector.Conditioned> CODEC = ExtraCodecs.either(
          RecordCodecBuilder.<ConditionedSelector>create(data -> data.group(
                  ExtraCodecs.optional("condition", MagicCodecs.LOOT_CONDITION)
                      .forGetter(ConditionedSelector::condition),
                  SelectorTypes.CODEC.fieldOf("value").forGetter(ConditionedSelector::other))
              .apply(data, ConditionedSelector::new)),
          SelectorTypes.CODEC)
      .xmap(
          e -> e.map(
              Function.identity(), selector -> new ConditionedSelector(Optional.empty(), selector)),
          Either::left);

  @Override
  public Optional<ServerCommandSource> select(EventContext context) {
    var source = other.select(context.lootContext());
    if (source == null) return Optional.empty();
    return condition
        .filter(lootCondition -> !lootCondition.test(LootUtil.build(
            new LootContextParameterSet.Builder(context.lootContext().getWorld())
                .add(LootContextParameters.ORIGIN, source.getPosition())
                .addOptional(LootContextParameters.THIS_ENTITY, source.getEntity())
                .build(LootContextTypes.COMMAND))))
        .<Optional<ServerCommandSource>>map(lootCondition -> Optional.empty())
        .orElseGet(() -> Optional.of(source));
  }
}
