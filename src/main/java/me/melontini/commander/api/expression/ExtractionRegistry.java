package me.melontini.commander.api.expression;

import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import net.minecraft.loot.context.LootContextParameter;

import java.util.function.Consumer;

public class ExtractionRegistry {

    public static void register(LootContextParameter<?> parameter, Consumer<ExtractionBuilder> builder) {
        ExtractionTypes.register(parameter, builder);
    }
}
