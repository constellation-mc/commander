package me.melontini.commander.impl.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin {

    @Unique
    private static final Codec<List<Command.Conditioned>> COMMANDER_CODEC = ExtraCodecs.list(Command.CODEC);

    @Unique
    private List<Command.Conditioned> commands;

    @ModifyReturnValue(at = @At("TAIL"), method = "toJson")
    private JsonElement commander$encodeCommands(JsonElement original) {
        if (!original.isJsonNull() && this.commands != null) {
            var result = COMMANDER_CODEC.encodeStart(JsonOps.INSTANCE, this.commands);
            if (result.error().isPresent()) throw new IllegalStateException(result.error().get().message());
            original.getAsJsonObject().add("commander:commands", result.get().orThrow());
        }
        return original;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "fromJson")
    private static AdvancementRewards commander$parseCommands(AdvancementRewards original, @Local(argsOnly = true) JsonObject object) {
        if (object.has("commander:commands")) {
            var result = COMMANDER_CODEC.parse(JsonOps.INSTANCE, object.get("commander:commands"));
            if (result.error().isPresent()) throw new JsonParseException(result.error().get().message());
            ((AdvancementRewardsMixin)(Object)original).commands = result.get().orThrow();
            for (Command.Conditioned command : ((AdvancementRewardsMixin)(Object)original).commands) {
                var r = command.validate(EventType.NULL);
                if (r.error().isPresent()) throw new JsonParseException(r.error().get().message());
            }
        }
        return original;
    }

    @Inject(at = @At("TAIL"), method = "apply")
    private void commander$applyCommands(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.commands == null) return;
        LootContextParameterSet parameterSet = (new LootContextParameterSet.Builder(player.getServerWorld())).add(LootContextParameters.THIS_ENTITY, player).add(LootContextParameters.ORIGIN, player.getPos()).build(LootContextTypes.ADVANCEMENT_REWARD);
        LootContext context = new LootContext.Builder(parameterSet).build(null);

        EventContext context1 = EventContext.builder(EventType.NULL)
                .addParameter(EventKey.LOOT_CONTEXT, context)
                .build();

        for (Command.Conditioned command : this.commands) {
            command.execute(context1);
        }
    }
}
