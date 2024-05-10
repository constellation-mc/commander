package me.melontini.commander.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.LootContextParameterRegistry;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.builtin.BuiltInEvents;
import me.melontini.commander.impl.builtin.BuiltInSelectors;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import me.melontini.commander.impl.expression.EvalUtils;
import me.melontini.commander.impl.util.loot.ArithmeticaLootNumberProvider;
import me.melontini.commander.impl.util.loot.ExpressionLootCondition;
import me.melontini.commander.impl.util.mappings.AmbiguousRemapper;
import me.melontini.commander.impl.util.mappings.MappingKeeper;
import me.melontini.commander.impl.util.mappings.MinecraftDownloader;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.loot.context.LootContextParameters.*;

@Accessors(fluent = true)
@Log4j2
public class Commander {

    public static final PrependingLogger LOGGER = PrependingLogger.get();
    public static final LootNumberProviderType ARITHMETICA_PROVIDER = LootNumberProviderTypes.register("commander:arithmetica", ExtraCodecs.toJsonSerializer(ArithmeticaLootNumberProvider.CODEC.codec()));
    public static final LootConditionType EXPRESSION_CONDITION = Registry.register(Registries.LOOT_CONDITION_TYPE, id("expression"), new LootConditionType(ExtraCodecs.toJsonSerializer(ExpressionLootCondition.CODEC.codec())));

    private static final Path BASE_PATH = Path.of(System.getProperty("user.home")).resolve(".commander");
    public static final String MINECRAFT_VERSION = getVersion();
    public static final Path COMMANDER_PATH = BASE_PATH.resolve(MINECRAFT_VERSION);

    @Getter
    private AmbiguousRemapper mappingKeeper;

    public static Identifier id(String path) {
        return new Identifier("commander", path);
    }

    private static Commander instance;

    public static void init() {
        instance = new Commander();
        instance.onInitialize();
    }

    public static Commander get() {
        return MakeSure.notNull(instance);
    }

    public void onInitialize() {
        try {
            var oldPath = FabricLoader.getInstance().getGameDir().resolve(".commander");
            if (Files.exists(oldPath)) {
                if (!Files.exists(BASE_PATH)) Files.move(oldPath, BASE_PATH);
                else {
                    Files.walkFileTree(oldPath, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return super.visitFile(file, attrs);
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
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

        ServerReloadersEvent.EVENT.register(context -> context.register(new DynamicEventManager()));
        EvalUtils.init();

        try {
            MinecraftDownloader.downloadMappings();

            CompletableFuture<MemoryMappingTree> offMojmap = CompletableFuture.supplyAsync(MappingKeeper::loadOffMojmap, Util.getMainWorkerExecutor());
            CompletableFuture<MemoryMappingTree> offTarget = CompletableFuture.supplyAsync(MappingKeeper::loadOffTarget, Util.getMainWorkerExecutor());
            mappingKeeper = new MappingKeeper(MappingKeeper.loadMojmapTarget(offMojmap.join(), offTarget.join()));
        } catch (Throwable t) {
            log.error("Failed to download and prepare mappings! Data access remapping will not work!!!", t);
            mappingKeeper = (cls, name) -> name;//Returning null will force it to traverse the hierarchy.
        }

        BuiltInEvents.init();
        BuiltInCommands.init();
        BuiltInSelectors.init();

        LootContextParameterRegistry.register(
                ORIGIN, TOOL,
                THIS_ENTITY, LAST_DAMAGE_PLAYER,
                KILLER_ENTITY, DIRECT_KILLER_ENTITY,
                DAMAGE_SOURCE, EXPLOSION_RADIUS,
                BLOCK_STATE, BLOCK_ENTITY);
    }

    @SneakyThrows(IOException.class)
    private static String getVersion() {
        @Cleanup var stream = new InputStreamReader(MinecraftDownloader.class.getResourceAsStream("/version.json"), StandardCharsets.UTF_8);
        JsonObject o = JsonParser.parseReader(stream).getAsJsonObject();
        return o.getAsJsonPrimitive("id").getAsString();
    }
}
