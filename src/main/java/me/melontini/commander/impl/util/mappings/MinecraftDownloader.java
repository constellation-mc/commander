package me.melontini.commander.impl.util.mappings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.impl.Commander;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.DeflaterInputStream;

@Log4j2
public final class MinecraftDownloader {

    private static final URL MANIFEST = url("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

    @SneakyThrows
    static URL url(String s) {
        return new URI(s).toURL();
    }

    @SneakyThrows
    public static void downloadMappings() {
        Path mappings = Commander.COMMANDER_PATH.resolve("mappings/server_mappings.bin");
        if (Files.exists(mappings)) return;
        var url = url(getManifest().getAsJsonObject("downloads").getAsJsonObject("server_mappings").get("url").getAsString());
        try (var stream = url.openStream()) {
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
            try (var defStream = new DeflaterInputStream(stream)) {
                Files.write(mappings, defStream.readAllBytes());
            }
        }
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
        try (var stream = url.openStream(); var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
