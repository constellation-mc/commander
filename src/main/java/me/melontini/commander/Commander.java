package me.melontini.commander;

import me.melontini.commander.command.builtin.BuiltInCommands;
import me.melontini.commander.command.selector.BuiltInSelectors;
import me.melontini.commander.data.DynamicEventManager;
import me.melontini.commander.event.builtin.BuiltInEvents;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

//TODO:
// Better validation during `apply`
// Wrap most common/server fabric events.
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
