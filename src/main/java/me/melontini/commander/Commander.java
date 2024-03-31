package me.melontini.commander;

import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.builtin.BuiltInEvents;
import me.melontini.commander.builtin.BuiltInSelectors;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

//TODO:
// Better validation during `apply`
// Macros with dynamic fields
public class Commander implements ModInitializer {

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
