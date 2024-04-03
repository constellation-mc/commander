package me.melontini.commander.impl.command;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.selector.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import me.melontini.commander.impl.util.MagicCodecs;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;
import java.util.function.Function;

public record ConditionedSelector(Optional<LootCondition> condition, Selector other) implements Selector.Conditioned {

    public static final Codec<? extends Selector.Conditioned> CODEC =  ExtraCodecs.either(RecordCodecBuilder.<ConditionedSelector>create(data -> data.group(
                ExtraCodecs.optional("condition", MagicCodecs.LOOT_CONDITION).forGetter(ConditionedSelector::condition),
                SelectorTypes.CODEC.fieldOf("value").forGetter(ConditionedSelector::other)
        ).apply(data, ConditionedSelector::new)), SelectorTypes.CODEC).xmap(e -> e.map(Function.identity(), selector -> new ConditionedSelector(Optional.empty(), selector)), Either::left);

    public Optional<ServerCommandSource> select(EventContext context) {
        var source = other.select(context.lootContext());
        if (source == null) return Optional.empty();
        if (condition.isEmpty()) return Optional.of(source);

        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(context.lootContext().getWorld());
        builder.add(LootContextParameters.ORIGIN, source.getPosition());
        builder.addOptional(LootContextParameters.THIS_ENTITY, source.getEntity());
        LootContext sourceContext = new LootContext.Builder(builder.build(LootContextTypes.COMMAND)).build(null);

        return condition.get().test(sourceContext) ? Optional.of(source) : Optional.empty();
    }
}
