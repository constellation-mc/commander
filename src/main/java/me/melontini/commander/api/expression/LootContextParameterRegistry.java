package me.melontini.commander.api.expression;

import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import net.minecraft.loot.context.LootContextParameter;

public class LootContextParameterRegistry {

    public static void register(LootContextParameter<?>... parameters) {
        for (LootContextParameter<?> parameter : parameters) {
            ExtractionTypes.register(parameter);
        }
    }
}
