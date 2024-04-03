package me.melontini.commander.impl;

import me.melontini.commander.api.util.Arithmetica;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.builtin.BuiltInEvents;
import me.melontini.commander.impl.builtin.BuiltInSelectors;
import me.melontini.commander.impl.event.data.DynamicEventManager;
import me.melontini.commander.impl.util.ArithmeticaLootNumberProvider;
import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
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
    public static final LootNumberProviderType ARITHMETICA_PROVIDER = LootNumberProviderTypes.register("commander:arithmetica", ExtraCodecs.toJsonSerializer(Arithmetica.CODEC.xmap(ArithmeticaLootNumberProvider::new, ArithmeticaLootNumberProvider::value)));

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
