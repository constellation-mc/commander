package me.melontini.commander.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.api.expression.LootContextParameterRegistry;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.builtin.BuiltInEvents;
import me.melontini.commander.impl.builtin.BuiltInSelectors;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import me.melontini.commander.impl.util.ArithmeticaLootNumberProvider;
import me.melontini.commander.impl.util.eval.EvalUtils;
import me.melontini.commander.impl.util.eval.ReflectiveMapStructure;
import me.melontini.commander.impl.util.mappings.MappingKeeper;
import me.melontini.commander.impl.util.mappings.MinecraftDownloader;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;

import static net.minecraft.loot.context.LootContextParameters.*;

@Log4j2
public class Commander implements ModInitializer {

    public static final PrependingLogger LOGGER = PrependingLogger.get();
    public static final LootNumberProviderType ARITHMETICA_PROVIDER = LootNumberProviderTypes.register("commander:arithmetica", ExtraCodecs.toJsonSerializer(Arithmetica.CODEC.xmap(ArithmeticaLootNumberProvider::new, ArithmeticaLootNumberProvider::value)));

    public static final Path COMMANDER_PATH = FabricLoader.getInstance().getGameDir().resolve(".commander");
    public static final String MINECRAFT_VERSION = getVersion();

    @Getter
    private static TinyRemapper remapper;

    public static Identifier id(String path) {
        return new Identifier("commander", path);
    }

    @Override
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
        MinecraftDownloader.downloadMappings();

        IMappingProvider provider = MappingKeeper.create(MappingKeeper.getMojmapTarget(), MappingKeeper.NAMESPACE, "mojang");
        remapper = TinyRemapper.newRemapper()
                .renameInvalidLocals(false)
                .withMappings(provider)
                .build();
        remapper.readInputs(remapper.createInputTag(), FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getOrigin().getPaths().toArray(Path[]::new));

        log.info("Scanning common context classes!");
        Util.getMainWorkerExecutor().submit(() -> {
            Set.of(
                    ServerPlayerEntity.class,
                    Vec3d.class, BlockPos.class,
                    BlockState.class, BlockEntity.class,
                    ItemStack.class, DamageSource.class,
                    ServerWorld.class
            ).forEach(aClass -> {
                do {
                    ReflectiveMapStructure.getAccessors(aClass);
                    for (Field field : aClass.getFields()) {
                        ReflectiveMapStructure.getAccessors(field.getType());
                    }
                    for (Method method : aClass.getMethods()) {
                        if (method.getReturnType() == void.class) continue;
                        ReflectiveMapStructure.getAccessors(method.getReturnType());
                    }
                    aClass = aClass.getSuperclass();
                }
                while (aClass.getSuperclass() != null);
            });
            log.info("Finished scanning classes!");
        });

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

    private static String getVersion() {
        JsonObject o = JsonParser.parseReader(new InputStreamReader(MinecraftDownloader.class.getResourceAsStream("/version.json"))).getAsJsonObject();
        return o.getAsJsonPrimitive("id").getAsString();
    }
}
