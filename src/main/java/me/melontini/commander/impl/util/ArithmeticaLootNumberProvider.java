package me.melontini.commander.impl.util;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import me.melontini.commander.api.util.Arithmetica;
import me.melontini.commander.impl.Commander;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

import java.util.Objects;

public class ArithmeticaLootNumberProvider implements LootNumberProvider {
    final Arithmetica value;

    ArithmeticaLootNumberProvider(Arithmetica value) {
        this.value = value;
    }

    public LootNumberProviderType getType() {
        return Commander.ARITHMETICA_PROVIDER;
    }

    public float nextFloat(LootContext context) {
        return this.value.asFloat(context);
    }

    public static ArithmeticaLootNumberProvider create(Arithmetica value) {
        return new ArithmeticaLootNumberProvider(value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            return Objects.equals(((ArithmeticaLootNumberProvider)o).value.toSource(), this.value.toSource());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.value.toSource().hashCode();
    }

    public static class Serializer implements JsonSerializer<ArithmeticaLootNumberProvider> {

        public void toJson(JsonObject jsonObject, ArithmeticaLootNumberProvider provider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("value", Arithmetica.CODEC.encodeStart(JsonOps.INSTANCE, provider.value).getOrThrow(true, string -> {
                throw new JsonParseException(string);
            }));
        }

        public ArithmeticaLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            JsonElement json = JsonHelper.getElement(jsonObject, "value");
            return new ArithmeticaLootNumberProvider(Arithmetica.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(true, string -> {
                throw new JsonParseException(string);
            }));
        }
    }
}
