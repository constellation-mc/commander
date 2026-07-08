package dev.zenfyr.commander.impl;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.zenfyr.commander.api.expression.LootContextParameterRegistry;
import dev.zenfyr.commander.impl.brigadier.BrigadierCommands;
import dev.zenfyr.commander.impl.expression.EvalUtils;
import dev.zenfyr.commander.impl.expression.extension.ExpressionLootCondition;
import dev.zenfyr.commander.impl.expression.extension.ExpressionLootNumberProvider;
import dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader;
import dev.zenfyr.commander.impl.util.NbtCodecs;
import dev.zenfyr.pulsar.api.codec.ExtraCodecs;
import dev.zenfyr.pulsar.api.resources.ServerReloadListenersEvent;
import dev.zenfyr.pulsar.api.util.TextUtil;
import java.util.List;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnstableApiUsage")
@Accessors(fluent = true)
public class Commander implements ModInitializer {

  public static final Logger LOGGER = LoggerFactory.getLogger(Commander.class);

  public static final LootNumberProviderType EXPRESSION_PROVIDER = Registry.register(
      BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE,
      id("expression"),
      new LootNumberProviderType(
          ExtraCodecs.toJsonSerializer(ExpressionLootNumberProvider.CODEC.codec())));

  public static final LootItemConditionType EXPRESSION_CONDITION = Registry.register(
      BuiltInRegistries.LOOT_CONDITION_TYPE,
      id("expression"),
      new LootItemConditionType(
          ExtraCodecs.toJsonSerializer(ExpressionLootCondition.CODEC.codec())));

  public static final AttachmentType<CompoundTag> DATA_ATTACHMENT =
      AttachmentRegistry.<CompoundTag>builder()
          .initializer(CompoundTag::new)
          .persistent(NbtCodecs.COMPOUND_CODEC)
          .buildAndRegister(id("persistent"));

  public static final DynamicCommandExceptionType EXPRESSION_EXCEPTION =
      new DynamicCommandExceptionType(object -> TextUtil.literal("Failed to evaluate: " + object));

  public static ResourceLocation id(String path) {
    return ResourceLocation.tryBuild("commander", path);
  }

  public void onInitialize() {
    ServerReloadListenersEvent.EVENT.listen(context -> {
      this.resetCaches();
      context.register(ExpressionLibraryLoader.RELOADER.location(), new ExpressionLibraryLoader());
    });

    ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.resetCaches());

    EvalUtils.init();
    BrigadierCommands.init();

    // Register vanilla parameter types
    List<LootContextParam<?>> params = List.of(
        LootContextParams.ORIGIN, LootContextParams.TOOL,
        LootContextParams.THIS_ENTITY, LootContextParams.LAST_DAMAGE_PLAYER,
        LootContextParams.KILLER_ENTITY, LootContextParams.DIRECT_KILLER_ENTITY,
        LootContextParams.DAMAGE_SOURCE, LootContextParams.EXPLOSION_RADIUS,
        LootContextParams.BLOCK_STATE, LootContextParams.BLOCK_ENTITY);

    params.forEach(p -> LootContextParameterRegistry.register(p.getName(), p));
  }

  private void resetCaches() {
    EvalUtils.resetCache();
  }
}
