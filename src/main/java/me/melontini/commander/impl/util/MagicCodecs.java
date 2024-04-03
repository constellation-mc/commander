package me.melontini.commander.impl.util;

import com.google.common.collect.BiMap;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class MagicCodecs {

    public static final GsonContextImpl lootContext = new GsonContextImpl(LootGsons.getConditionGsonBuilder().create());

    public static final Codec<LootCondition> LOOT_CONDITION = Codecs.exceptionCatching(Codecs.JSON_ELEMENT.flatXmap(element -> {
        if (!element.isJsonObject()) return DataResult.error(() -> "'%s' not a JsonObject".formatted(element));
        JsonObject object = element.getAsJsonObject();
        if (object.get("condition") == null) return DataResult.error(() -> "Missing required 'condition' field!");

        LootConditionType type = Registries.LOOT_CONDITION_TYPE.get(Identifier.tryParse(object.get("condition").getAsString()));
        if (type == null)
            return DataResult.error(() -> "No such condition type '%s'".formatted(object.get("condition").getAsString()));
        return DataResult.success((LootCondition) type.getJsonSerializer().fromJson(object, lootContext));
    }, condition -> {
        JsonObject object = new JsonObject();
        condition.getType().getJsonSerializer().toJson(object, Utilities.cast(condition), lootContext);
        return DataResult.success(object);
    }));

    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> cls) {
        return Codec.STRING.comapFlatMap(string -> {
            try {
                return DataResult.success(Enum.valueOf(cls, string.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                return DataResult.error(() -> "No such enum constant %s!".formatted(string));
            }
        }, t -> t.name().toLowerCase(Locale.ROOT));
    }

    public static <T> Codec<T> mapLookup(BiMap<Identifier, T> lookup) {
        return Identifier.CODEC.flatXmap(
                identifier -> Optional.ofNullable(lookup.get(identifier))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown type: %s".formatted(identifier))),
                eventType -> Optional.ofNullable(lookup.inverse().get(eventType))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown type: %s".formatted(eventType))));
    }

    public static final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {

        private final Gson gson;

        public GsonContextImpl(Gson gson) {
            this.gson = gson;
        }

        @Override
        public JsonElement serialize(Object src) {
            return gson.toJsonTree(src);
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc) {
            return gson.toJsonTree(src, typeOfSrc);
        }

        @Override
        public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
            return gson.fromJson(json, typeOfT);
        }
    }
}
