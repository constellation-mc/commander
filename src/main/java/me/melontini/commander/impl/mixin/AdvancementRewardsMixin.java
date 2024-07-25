package me.melontini.commander.impl.mixin;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.event.EventKey;
import me.melontini.commander.api.event.EventType;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin {

  @Unique private List<Command.Conditioned> commands;

  // https://gist.github.com/kvverti/dec17e824922e1974313b8beadc621c5
  @ModifyArg(
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"),
      index = 0,
      method = "<clinit>",
      remap = false)
  private static Function<
          RecordCodecBuilder.Instance<AdvancementRewards>,
          ? extends App<RecordCodecBuilder.Mu<AdvancementRewards>, AdvancementRewards>>
      commander$modifyCodec(
          Function<
                  RecordCodecBuilder.Instance<AdvancementRewards>,
                  ? extends App<RecordCodecBuilder.Mu<AdvancementRewards>, AdvancementRewards>>
              builder) {
    MapCodec<AdvancementRewards> mapCodec = RecordCodecBuilder.mapCodec(builder);
    Codec<List<Command.Conditioned>> commanderCodec = ExtraCodecs.list(Command.CODEC.codec());

    return data -> data.group(
            mapCodec.forGetter(Function.identity()),
            ExtraCodecs.optional("commander:commands", commanderCodec, Collections.emptyList())
                .forGetter(object -> ((AdvancementRewardsMixin) (Object) object).commands))
        .apply(data, (advancementRewards, commands) -> {
          ((AdvancementRewardsMixin) (Object) advancementRewards).commands = commands;
          return advancementRewards;
        });
  }

  @Inject(at = @At("TAIL"), method = "apply")
  private void commander$applyCommands(ServerPlayerEntity player, CallbackInfo ci) {
    if (this.commands == null) return;
    LootContextParameterSet parameterSet = new LootContextParameterSet.Builder(
            player.getServerWorld())
        .add(LootContextParameters.THIS_ENTITY, player)
        .add(LootContextParameters.ORIGIN, player.getPos())
        .build(LootContextTypes.ADVANCEMENT_REWARD);
    LootContext context = new LootContext.Builder(parameterSet).build(Optional.empty());

    EventContext context1 = EventContext.builder(EventType.NULL)
        .addParameter(EventKey.LOOT_CONTEXT, context)
        .build();

    for (Command.Conditioned command : this.commands) {
      command.execute(context1);
    }
  }
}
