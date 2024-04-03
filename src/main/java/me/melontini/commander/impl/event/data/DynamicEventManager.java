package me.melontini.commander.impl.event.data;

import com.google.common.collect.Sets;
import lombok.experimental.Accessors;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.event.Subscription;
import me.melontini.commander.impl.event.data.types.EventTypes;
import me.melontini.dark_matter.api.data.codecs.JsonCodecDataLoader;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class DynamicEventManager extends JsonCodecDataLoader<List<? extends Subscription<?>>> implements IdentifiableResourceReloadListener {

    public static final ReloaderType<DynamicEventManager> RELOADER = ReloaderType.create(new Identifier("commander:events"));

    final Map<EventType, Object> customData = new IdentityHashMap<>();

    public DynamicEventManager() {
        super(RELOADER.identifier(), SubscriptionImpl.CODEC);
    }

    public static <T> T getData(MinecraftServer server, EventType type) {
        return server.dm$getReloader(DynamicEventManager.RELOADER).getData(type);
    }

    public <T> T getData(EventType type) {
        return (T) this.customData.get(type);
    }

    @Override
    protected void apply(Map<Identifier, List<? extends Subscription<?>>> parsed, ResourceManager manager) {
        parsed.values().stream().flatMap(Collection::stream).collect(Collectors.groupingBy(Subscription::type)).forEach((eventType, subscriptions) -> {
            var finalizer = eventType.get(EventType.FINALIZER);
            if (finalizer.isPresent()) {
                this.customData.put(eventType, finalizer.get().apply(Collections.unmodifiableList(subscriptions)));
                return;
            }
            this.customData.put(eventType, subscriptions.stream().flatMap(s -> s.list().stream()).toList());
        });

        Sets.difference(EventTypes.types(), customData.keySet()).forEach(type -> {
            var finalizer = type.get(EventType.FINALIZER);
            if (finalizer.isPresent()) {
                this.customData.put(type, finalizer.get().apply(Collections.emptyList()));
                return;
            }
            this.customData.put(type, Collections.emptyList());
        });
    }
}
