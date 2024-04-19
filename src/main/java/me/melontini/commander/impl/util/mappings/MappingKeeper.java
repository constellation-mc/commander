package me.melontini.commander.impl.util.mappings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.objectweb.asm.Type;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.InflaterInputStream;

@Log4j2
public record MappingKeeper(MemoryMappingTree mojmapTarget) implements AmbiguousRemapper {

    public static final String NAMESPACE = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();

    @SneakyThrows
    public static MemoryMappingTree loadMojmapTarget(MemoryMappingTree offMojmap, MemoryMappingTree offTarget) {
        log.info("Merging mappings...");

        var tree = new MemoryMappingTree();
        MemoryMappingTree temp = new MemoryMappingTree();
        offMojmap.accept(temp);
        if (!NAMESPACE.equals("mojang")) offTarget.accept(temp);
        temp.accept(new MappingSourceNsSwitch(tree, "mojang", true));
        tree.getClass("net/minecraft/server/MinecraftServer").setDstName("net/minecraft/server/MinecraftServer", tree.getNamespaceId(NAMESPACE));
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffMojmap() {
        if (NAMESPACE.equals("mojang")) return null;
        log.info("Loading official->mojmap mappings...");
        Path path = FabricLoader.getInstance().getModContainer("commander").orElseThrow().findPath("commander/mappings/%s.bin".formatted(getVersion())).orElseThrow();

        var tree = new MemoryMappingTree();
        MappingReader.read(new InputStreamReader(new InflaterInputStream(Files.newInputStream(path))), tree);
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffTarget() {
        log.info("Loading official->{} mappings...", NAMESPACE);

        var tree = new MemoryMappingTree();
        MappingReader.read(new InputStreamReader(Objects.requireNonNull(MappingKeeper.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny"), "mappings/mappings.tiny is not available?")), tree);
        return tree;
    }

    public String getFieldOrMethod(Class<?> cls, String name) {
        var clsData = mojmapTarget.getClass(Type.getInternalName(cls), mojmapTarget.getNamespaceId(NAMESPACE));
        if (clsData == null) return null;

        for (MappingTree.MethodMapping method : clsData.getMethods()) {
            if (Objects.equals(method.getSrcName(), name)) {
                var srcDesc = method.getSrcDesc();
                if (srcDesc != null && srcDesc.startsWith("()") && !srcDesc.endsWith("V")) return method.getName(NAMESPACE);
            }
        }

        for (MappingTree.FieldMapping field : clsData.getFields()) {
            if (Objects.equals(field.getSrcName(), name)) return field.getName(NAMESPACE);
        }
        return null;
    }

    public static String getVersion() {
        JsonObject o = JsonParser.parseReader(new InputStreamReader(MappingKeeper.class.getResourceAsStream("/version.json"))).getAsJsonObject();
        return o.getAsJsonPrimitive("id").getAsString();
    }
}
