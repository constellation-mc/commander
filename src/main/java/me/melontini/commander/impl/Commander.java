package me.melontini.commander.impl;

import static java.util.concurrent.CompletableFuture.*;
import static net.minecraft.loot.context.LootContextParameters.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.LootContextParameterRegistry;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.builtin.BuiltInEvents;
import me.melontini.commander.impl.builtin.BuiltInSelectors;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.convert.RegistryAccessStruct;
import me.melontini.commander.impl.expression.library.ExpressionLibraryLoader;
import me.melontini.commander.impl.util.NbtCodecs;
import me.melontini.commander.impl.util.loot.ArithmeticaLootNumberProvider;
import me.melontini.commander.impl.util.loot.ExpressionLootCondition;
import me.melontini.commander.impl.util.mappings.AmbiguousRemapper;
import me.melontini.commander.impl.util.mappings.MappingKeeper;
import me.melontini.commander.impl.util.mappings.MinecraftDownloader;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@SuppressWarnings("UnstableApiUsage")
@Accessors(fluent = true)
@Log4j2
public class Commander {

  public static final LootNumberProviderType ARITHMETICA_PROVIDER =
      LootNumberProviderTypes.register(
          "commander:arithmetica",
          ExtraCodecs.toJsonSerializer(ArithmeticaLootNumberProvider.CODEC.codec()));
  public static final LootConditionType EXPRESSION_CONDITION = Registry.register(
      Registries.LOOT_CONDITION_TYPE,
      id("expression"),
      new LootConditionType(ExtraCodecs.toJsonSerializer(ExpressionLootCondition.CODEC.codec())));

  private static final Path BASE_PATH =
      Path.of(System.getProperty("user.home")).resolve(".commander");
  public static final String MINECRAFT_VERSION = getVersion();
  public static final Path COMMANDER_PATH = BASE_PATH.resolve(MINECRAFT_VERSION);

  public static final AttachmentType<NbtCompound> DATA_ATTACHMENT =
      AttachmentRegistry.<NbtCompound>builder()
          .initializer(NbtCompound::new)
          .persistent(NbtCodecs.COMPOUND_CODEC)
          .buildAndRegister(id("persistent"));

  public static final DynamicCommandExceptionType EXPRESSION_EXCEPTION =
      new DynamicCommandExceptionType(object -> TextUtil.literal("Failed to evaluate: " + object));

  @Getter
  private AmbiguousRemapper mappingKeeper;

  public static Identifier id(String path) {
    return new Identifier("commander", path);
  }

  private static Supplier<Commander> instance = () -> {
    throw new NullPointerException("Commander instance requested too early!");
  };

  public static void init() {
    var cmd = new Commander();
    cmd.onInitialize();
    instance = () -> cmd;
  }

  public static Commander get() {
    return instance.get();
  }

  public void onInitialize() {
    if (!Files.exists(COMMANDER_PATH)) {
      Exceptions.run(() -> Files.createDirectories(COMMANDER_PATH));
      try {
        // Some users don't like junk in their home folder and windows is very special.
        if (BASE_PATH.getFileSystem().supportedFileAttributeViews().contains("dos"))
          Files.setAttribute(BASE_PATH, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
      } catch (IOException ignored) {
        log.warn("Failed to hide the .commander folder");
      }
    }

    ServerReloadersEvent.EVENT.register(context -> {
      this.resetCaches();
      context.register(new ExpressionLibraryLoader());
      context.register(new DynamicEventManager());
    });

    ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.resetCaches());

    EvalUtils.init();
    this.loadMappings();

    // Init built-ins
    BuiltInEvents.init();
    BuiltInCommands.init();
    BuiltInSelectors.init();

    // Register vanilla parameter types
    LootContextParameterRegistry.register(
        ORIGIN, TOOL,
        THIS_ENTITY, LAST_DAMAGE_PLAYER,
        KILLER_ENTITY, DIRECT_KILLER_ENTITY,
        DAMAGE_SOURCE, EXPLOSION_RADIUS,
        BLOCK_STATE, BLOCK_ENTITY);
  }

  private void resetCaches() {
    EvalUtils.resetCache();
    RegistryAccessStruct.resetCache();
  }

  private void loadMappings() {
    if (MappingKeeper.NAMESPACE.equals("mojang")) {
      mappingKeeper = (cls, name) -> name; // Nothing to remap.
      return;
    }

    var offTarget = supplyAsync(MappingKeeper::loadOffTarget, Util.getMainWorkerExecutor());
    var offMojmap = runAsync(MinecraftDownloader::downloadMappings, Util.getMainWorkerExecutor())
        .thenApplyAsync(unused -> MappingKeeper.loadOffMojmap(), Util.getMainWorkerExecutor());

    mappingKeeper = Exceptions.<AmbiguousRemapper>supplyAsResult(() ->
            new MappingKeeper(MappingKeeper.loadMojmapTarget(offMojmap.join(), offTarget.join())))
        .ifErrPresent(t -> log.error(
            "Failed to download and prepare mappings! Data access remapping will not work!!!",
            Exceptions.unwrap(t)))
        .flatmapErr(t -> Result.ok((cls, name) -> name))
        .value()
        .orElseThrow();
  }

  // Returns the current MC version parsed from included version.json
  private static String getVersion() {
    return Exceptions.supplyAsResult(() -> {
          try (var stream = new InputStreamReader(
              MinecraftDownloader.class.getResourceAsStream("/version.json"),
              StandardCharsets.UTF_8)) {
            JsonObject o = JsonParser.parseReader(stream).getAsJsonObject();
            return o.getAsJsonPrimitive("id").getAsString();
          }
        })
        .ifErrPresent(e -> {
          throw new IllegalStateException(
              "Failed to read 'version.json' included in the Minecraft jar!");
        })
        .value()
        .orElseThrow();
  }
}
