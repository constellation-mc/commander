package me.melontini.commander.impl;

import static net.minecraft.loot.context.LootContextParameters.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.LootContextParameterRegistry;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.builtin.BuiltInEvents;
import me.melontini.commander.impl.builtin.BuiltInSelectors;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.expression.extensions.convert.RegistryAccessStruct;
import me.melontini.commander.impl.util.NbtCodecs;
import me.melontini.commander.impl.util.loot.ArithmeticaLootNumberProvider;
import me.melontini.commander.impl.util.loot.ExpressionLootCondition;
import me.melontini.commander.impl.util.mappings.AmbiguousRemapper;
import me.melontini.commander.impl.util.mappings.MappingKeeper;
import me.melontini.commander.impl.util.mappings.MinecraftDownloader;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
@Log4j2
public class Commander {

  public static final PrependingLogger LOGGER = PrependingLogger.get();
  public static final LootNumberProviderType ARITHMETICA_PROVIDER = Registry.register(
      Registries.LOOT_NUMBER_PROVIDER_TYPE,
      id("arithmetica"),
      new LootNumberProviderType(ArithmeticaLootNumberProvider.CODEC));
  public static final LootConditionType EXPRESSION_CONDITION = Registry.register(
      Registries.LOOT_CONDITION_TYPE,
      id("expression"),
      new LootConditionType(ExpressionLootCondition.CODEC));

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

  @Getter
  @Setter
  private @Nullable MinecraftServer currentServer;

  public static Identifier id(String path) {
    return Identifier.of("commander", path);
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
    try {
      var oldPath = FabricLoader.getInstance().getGameDir().resolve(".commander");
      if (Files.exists(oldPath)) {
        if (!Files.exists(BASE_PATH)) Files.move(oldPath, BASE_PATH);
        else {
          Files.walkFileTree(oldPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.delete(dir);
              return super.postVisitDirectory(dir, exc);
            }
          });
        }
      }
    } catch (IOException e) {
      log.error("Failed to move old .commander folder!", e);
    }

    if (!Files.exists(COMMANDER_PATH)) {
      Exceptions.run(() -> Files.createDirectories(COMMANDER_PATH));
      try {
        if (BASE_PATH.getFileSystem().supportedFileAttributeViews().contains("dos"))
          Files.setAttribute(BASE_PATH, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
      } catch (IOException ignored) {
        LOGGER.warn("Failed to hide the .commander folder");
      }
    }

    ServerReloadersEvent.EVENT.register(context -> {
      this.resetCaches();
      context.register(new DynamicEventManager());
    });

    ServerLifecycleEvents.SERVER_STARTING.register(server -> this.currentServer = server);
    ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
      this.currentServer = null;
      this.resetCaches();
    });

    EvalUtils.init();
    this.loadMappings();

    BuiltInEvents.init();
    BuiltInCommands.init();
    BuiltInSelectors.init();

    LootContextParameterRegistry.register(
        ORIGIN, TOOL,
        THIS_ENTITY, LAST_DAMAGE_PLAYER,
        ATTACKING_ENTITY, DIRECT_ATTACKING_ENTITY,
        DAMAGE_SOURCE, EXPLOSION_RADIUS,
        BLOCK_STATE, BLOCK_ENTITY,
                ENCHANTMENT_LEVEL, ENCHANTMENT_ACTIVE);
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

    try {
      CompletableFuture<MemoryMappingTree> offTarget =
          CompletableFuture.supplyAsync(MappingKeeper::loadOffTarget, Util.getMainWorkerExecutor());
      CompletableFuture<MemoryMappingTree> offMojmap = CompletableFuture.runAsync(
              MinecraftDownloader::downloadMappings, Util.getMainWorkerExecutor())
          .thenApplyAsync(unused -> MappingKeeper.loadOffMojmap(), Util.getMainWorkerExecutor());
      mappingKeeper =
          new MappingKeeper(MappingKeeper.loadMojmapTarget(offMojmap.join(), offTarget.join()));
    } catch (Throwable t) {
      log.error(
          "Failed to download and prepare mappings! Data access remapping will not work!!!", t);
      mappingKeeper =
          (cls, name) -> name; // Returning null will force it to traverse the hierarchy.
    }
  }

  @SneakyThrows(IOException.class)
  private static String getVersion() {
    @Cleanup
    var stream = new InputStreamReader(
        MinecraftDownloader.class.getResourceAsStream("/version.json"), StandardCharsets.UTF_8);
    JsonObject o = JsonParser.parseReader(stream).getAsJsonObject();
    return o.getAsJsonPrimitive("id").getAsString();
  }
}
