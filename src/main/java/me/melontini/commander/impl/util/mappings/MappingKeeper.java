package me.melontini.commander.impl.util.mappings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.zip.InflaterInputStream;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import me.melontini.dark_matter.api.base.util.MakeSure;
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

  public static MemoryMappingTree loadMojmapTarget(
      MemoryMappingTree offMojmap, MemoryMappingTree offTarget) {
    log.info("Merging mappings...");

    var merged = new MemoryMappingTree();
    try {
      MemoryMappingTree temp = new MemoryMappingTree();
      offMojmap.accept(temp);
      offTarget.accept(temp);
      temp.accept(new MappingSourceNsSwitch(merged, "mojang", true));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to merge official and mojang mappings!", e);
    }

    MakeSure.notNull(
            merged.getClass("net/minecraft/server/MinecraftServer"),
            "No built-in MinecraftServer mapping?")
        .setDstName("net/minecraft/server/MinecraftServer", merged.getNamespaceId(NAMESPACE));
    return merged;
  }

  @Nullable public static MemoryMappingTree loadOffMojmap() {
    log.info("Loading official->mojmap mappings...");

    var mappings = new MemoryMappingTree();
    try (var reader = new InputStreamReader(
        new InflaterInputStream(
            Files.newInputStream(Commander.COMMANDER_PATH.resolve("mappings/server_mappings.bin"))),
        StandardCharsets.UTF_8)) {
      MappingReader.read(reader, new MappingSourceNsSwitch(mappings, "target"));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load official-mojmap mappings!", e);
    }

    mappings.setSrcNamespace("official");
    mappings.setDstNamespaces(List.of("mojang"));
    return mappings;
  }

  public static MemoryMappingTree loadOffTarget() {
    log.info("Loading official->{} mappings...", NAMESPACE);

    try (var reader = new InputStreamReader(
        MakeSure.notNull(
            MappingKeeper.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny"),
            "mappings/mappings.tiny is not available? Are you running a fork of Fabric?"),
        StandardCharsets.UTF_8)) {
      var tree = new MemoryMappingTree();
      MappingReader.read(reader, tree);
      return tree;
    } catch (IOException e) {
      throw new IllegalStateException(("Failed to read official->%s mappings! "
              + "Those mappings are provided by the loader and must be readable!")
          .formatted(NAMESPACE));
    }
  }

  @Override
  public @Nullable String getFieldOrMethod(Class<?> cls, String name) {
    var clsData = mojmapTarget()
        .getClass(Type.getInternalName(cls), mojmapTarget().getNamespaceId(NAMESPACE));
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
