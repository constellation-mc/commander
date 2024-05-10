package me.melontini.commander.impl.util.mappings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.MappingWriter;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;

@Log4j2
public final class MinecraftDownloader {

    private static final URL MANIFEST = url("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

    @SneakyThrows
    static URL url(String s) {
        return new URL(s);
    }

    @SneakyThrows
    public static void downloadMappings() {
        Path mappings = Commander.COMMANDER_PATH.resolve("mappings/server_mappings.bin");
        if (Files.exists(mappings)) return;
        var url = url(getManifest().getAsJsonObject("downloads").getAsJsonObject("server_mappings").get("url").getAsString());

        var parent = Objects.requireNonNull(mappings.getParent());
        Files.createDirectories(parent);

        log.info("Downloading {}...", Objects.requireNonNull(mappings.getFileName()).toString());
        Files.writeString(parent.resolve("LICENSE.txt"), """
                    (c) 2020 Microsoft Corporation.
                    These mappings are provided "as-is" and you bear the risk of using them.
                    You may copy and use the mappings for development purposes, but you may not redistribute the mappings complete and unmodified.
                    Microsoft makes no warranties, express or implied, with respect to the mappings provided here.
                    Use and modification of this document or the source code (in any form) of Minecraft: Java Edition is governed by the Minecraft End User License Agreement available at https://account.mojang.com/documents/minecraft_eula.
                    """);

        @Cleanup var reader = new InputStreamReader(url.openStream());
        @Cleanup var outputStream = Files.newOutputStream(mappings);

        MemoryMappingTree tree = new MemoryMappingTree();
        MappingReader.read(reader, tree);

        @Cleanup var output =  new OutputStreamWriter(new DeflaterOutputStream(outputStream), StandardCharsets.UTF_8);
        tree.accept(MappingWriter.create(output, MappingFormat.TSRG_2_FILE));
    }

    public static JsonObject getManifest() {
        var o = downloadObject(MANIFEST);
        for (JsonElement versions : o.getAsJsonArray("versions")) {
            if (Commander.MINECRAFT_VERSION.equals(versions.getAsJsonObject().get("id").getAsString())) {
                return downloadObject(url(versions.getAsJsonObject().get("url").getAsString()));
            }
        }
        throw new IllegalStateException("Unknown version '%s'".formatted(Commander.MINECRAFT_VERSION));
    }

    @SneakyThrows
    private static JsonObject downloadObject(URL url) {
        @Cleanup var reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
        return JsonParser.parseReader(reader).getAsJsonObject();
    }
}
