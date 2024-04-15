package me.melontini.commander.impl.util.mappings;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.objectweb.asm.Type;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Log4j2
public final class MappingKeeper {

    @Getter(lazy = true)
    private static final MemoryMappingTree offMojmap = loadOffMojmap();
    @Getter(lazy = true)
    private static final MemoryMappingTree offTarget = loadOffTarget();
    @Getter(lazy = true)
    private static final MemoryMappingTree mojmapTarget = loadMojmapTarget();

    @SneakyThrows
    public static MemoryMappingTree loadMojmapTarget() {
        var tree = new MemoryMappingTree();
        MemoryMappingTree temp = new MemoryMappingTree();
        getOffMojmap().accept(temp);
        getOffTarget().accept(temp);
        log.info("Merging mappings...");
        temp.accept(new MappingSourceNsSwitch(tree, "mojang", true));
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffMojmap() {
        var tree = new MemoryMappingTree();
        log.info("Loading official->mojmap mappings...");
        MappingReader.read(FabricLoader.getInstance().getGameDir().resolve("commander/mappings/client_mappings.txt"), new MappingSourceNsSwitch(tree, "target"));
        tree.setSrcNamespace("official");
        tree.setDstNamespaces(List.of("mojang"));
        return tree;
    }

    @SneakyThrows
    public static MemoryMappingTree loadOffTarget() {
        var tree = new MemoryMappingTree();
        log.info("Loading official->named mappings...");
        MappingReader.read(new InputStreamReader(Objects.requireNonNull(MappingKeeper.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny"), "mappings/mappings.tiny is not available?")), tree);
        return tree;
    }

    public static String toNamed(Field field) {
        int id = getMojmapTarget().getNamespaceId(FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());
        var cls = getMojmapTarget().getClass(Type.getInternalName(field.getDeclaringClass()), id);
        if (cls == null) return field.getName();
        var fld = cls.getField(field.getName(), Type.getDescriptor(field.getType()), id);
        if (fld == null) return field.getName();
        return fld.getName("mojang");
    }

    public static String toNamed(Method method) {
        int id = getMojmapTarget().getNamespaceId(FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());
        var cls = getMojmapTarget().getClass(Type.getInternalName(method.getDeclaringClass()), id);
        if (cls == null) return method.getName();
        var mthd = cls.getMethod(method.getName(), Type.getMethodDescriptor(method), id);
        if (mthd == null) return method.getName();
        return mthd.getName("mojang");
    }
}
