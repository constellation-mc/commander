package me.melontini.commander.impl.util.mappings;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.zip.InflaterInputStream;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

@Log4j2
public record MappingKeeper(MemoryMappingTree mojmapTarget) implements AmbiguousRemapper {

  public static final String NAMESPACE =
      FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();

  @SneakyThrows
  public static MemoryMappingTree loadMojmapTarget(
      MemoryMappingTree offMojmap, MemoryMappingTree offTarget) {
    log.info("Merging mappings...");

    var tree = new MemoryMappingTree();
    MemoryMappingTree temp = new MemoryMappingTree();
    offMojmap.accept(temp);
    offTarget.accept(temp);
    temp.accept(new MappingSourceNsSwitch(tree, "mojang", true));
    Objects.requireNonNull(
            tree.getClass("net/minecraft/server/MinecraftServer"),
            "No built-in MinecraftServer mapping?")
        .setDstName("net/minecraft/server/MinecraftServer", tree.getNamespaceId(NAMESPACE));
    return tree;
  }

  @SneakyThrows
  @Nullable public static MemoryMappingTree loadOffMojmap() {
    log.info("Loading official->mojmap mappings...");

    var tree = new MemoryMappingTree();
    @Cleanup
    var reader = new InputStreamReader(
        new InflaterInputStream(
            Files.newInputStream(Commander.COMMANDER_PATH.resolve("mappings/server_mappings.bin"))),
        StandardCharsets.UTF_8);
    MappingReader.read(reader, new MappingSourceNsSwitch(tree, "target"));
    tree.setSrcNamespace("official");
    tree.setDstNamespaces(List.of("mojang"));
    return tree;
  }

  @SneakyThrows
  public static MemoryMappingTree loadOffTarget() {
    log.info("Loading official->{} mappings...", NAMESPACE);

    var tree = new MemoryMappingTree();
    @Cleanup
    var reader = new InputStreamReader(
        Objects.requireNonNull(
            MappingKeeper.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny"),
            "mappings/mappings.tiny is not available?"),
        StandardCharsets.UTF_8);
    MappingReader.read(reader, tree);
    return tree;
  }

  @Override
  public @Nullable String getFieldOrMethod(Class<?> cls, String name) {
    var clsData =
        mojmapTarget.getClass(Type.getInternalName(cls), mojmapTarget.getNamespaceId(NAMESPACE));
    if (clsData == null) return null;

    for (MappingTree.MethodMapping method : clsData.getMethods()) {
      if (Objects.equals(method.getSrcName(), name)) {
        var srcDesc = method.getSrcDesc();
        if (srcDesc != null && srcDesc.startsWith("()") && !srcDesc.endsWith("V"))
          return method.getName(NAMESPACE);
      }
    }

    for (MappingTree.FieldMapping field : clsData.getFields()) {
      if (Objects.equals(field.getSrcName(), name)) return field.getName(NAMESPACE);
    }
    return null;
  }
}
