package me.melontini.commander;

import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.builtin.BuiltInEvents;
import me.melontini.commander.builtin.BuiltInSelectors;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.util.math.ArithmeticaLootNumberProvider;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.Identifier;

//TODO:
// Better validation during `apply`
// Scoreboard fields
public class Commander implements ModInitializer {

    public static final PrependingLogger LOGGER = PrependingLogger.get();
    public static final LootNumberProviderType ARITHMETICA_PROVIDER = LootNumberProviderTypes.register("commander:arithmetica", new ArithmeticaLootNumberProvider.Serializer());;

    public static Identifier id(String path) {
        return new Identifier("commander", path);
    }

    @Override
    public void onInitialize() {
        ServerReloadersEvent.EVENT.register(context -> context.register(new DynamicEventManager()));

        BuiltInEvents.init();
        BuiltInCommands.init();
        BuiltInSelectors.init();
    }
}
