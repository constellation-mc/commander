package me.melontini.commander.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.Arithmetica;
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
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.loot.context.LootContextParameters.*;

@Accessors(fluent = true)
@Log4j2
public class Commander {

    public static final PrependingLogger LOGGER = PrependingLogger.get();
    public static final LootNumberProviderType ARITHMETICA_PROVIDER = Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, id("arithmetica"), new LootNumberProviderType(Arithmetica.CODEC.xmap(ArithmeticaLootNumberProvider::new, ArithmeticaLootNumberProvider::value)));
    public static final LootConditionType EXPRESSION_CONDITION = Registry.register(Registries.LOOT_CONDITION_TYPE, id("expression"), new LootConditionType(ExpressionLootCondition.CODEC));

    public static final Path COMMANDER_PATH = FabricLoader.getInstance().getGameDir().resolve(".commander");

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
        if (!Files.exists(COMMANDER_PATH)) {
            Exceptions.run(() -> Files.createDirectories(COMMANDER_PATH));
            try {
                if (COMMANDER_PATH.getFileSystem().supportedFileAttributeViews().contains("dos"))
                    Files.setAttribute(COMMANDER_PATH, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException ignored) {
                LOGGER.warn("Failed to hide the .commander folder");
            }
        }

        ServerReloadersEvent.EVENT.register(context -> context.register(new DynamicEventManager()));
        EvalUtils.init();

        try {
            CompletableFuture<MemoryMappingTree> offMojmap = CompletableFuture.supplyAsync(MappingKeeper::loadOffMojmap, Util.getMainWorkerExecutor());
            CompletableFuture<MemoryMappingTree> offTarget = CompletableFuture.supplyAsync(MappingKeeper::loadOffTarget, Util.getMainWorkerExecutor());
            mappingKeeper = new MappingKeeper(MappingKeeper.loadMojmapTarget(offMojmap.join(), offTarget.join()));
        } catch (Throwable t) {
            log.error("Failed to prepare mappings! Data access remapping will not work!!!", t);
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
}
