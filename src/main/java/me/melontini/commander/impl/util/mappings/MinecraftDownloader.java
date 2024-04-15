package me.melontini.commander.impl.util.mappings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtensionMethod(Files.class)
@Log4j2
public final class MinecraftDownloader {

    private static final URL MANIFEST = url("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");

    @SneakyThrows
    static URL url(String s) {
        return new URL(s);
    }

    @SneakyThrows
    public static void downloadMappings() {
        Path mappings = FabricLoader.getInstance().getGameDir().resolve("commander/mappings/client_mappings.txt");
        if (mappings.exists()) return;
        downloadIfNotExists(mappings, url(getManifest().getAsJsonObject("downloads")
                .getAsJsonObject("client_mappings").get("url").getAsString()));
    }

    @SneakyThrows
    public static void downloadIfNotExists(Path path, URL url) {
        if (!path.exists()) {
            try (var stream = url.openStream()) {
                path.getParent().createDirectories();

                log.info("Downloading {}...", path.getFileName().toString());
                path.write(stream.readAllBytes());
            }
        }
    }

    public static JsonObject getManifest() {
        var version = getVersion();
        var o = downloadObject(MANIFEST);
        for (JsonElement versions : o.getAsJsonArray("versions")) {
            if (version.equals(versions.getAsJsonObject().get("id").getAsString())) {
                return downloadObject(url(versions.getAsJsonObject().get("url").getAsString()));
            }
        }
        throw new IllegalStateException("Unknown version '%s'".formatted(version));
    }

    private static String getVersion() {
        JsonObject o = JsonParser.parseReader(new InputStreamReader(MinecraftDownloader.class.getResourceAsStream("/version.json"))).getAsJsonObject();
        return o.getAsJsonPrimitive("id").getAsString();
    }

    @SneakyThrows
    private static JsonObject downloadObject(URL url) {
        return JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
    }
}
