package me.melontini.commander.impl.util;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import java.lang.reflect.Type;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.registry.Registries;
import net.minecraft.util.JsonSerializableType;

@UtilityClass
public class MagicCodecs {

  public static final GsonContextImpl lootContext =
      new GsonContextImpl(LootGsons.getConditionGsonBuilder().create());

  public static final Codec<LootCondition> LOOT_CONDITION = ExtraCodecs.jsonSerializerDispatch(
      "condition",
      Registries.LOOT_CONDITION_TYPE.getCodec(),
      LootCondition::getType,
      JsonSerializableType::getJsonSerializer,
      lootContext);

  public static final class GsonContextImpl
      implements JsonSerializationContext, JsonDeserializationContext {

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
