package me.melontini.commander.api.command.selector;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.command.ConditionedSelector;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public interface Selector {

    Codec<Conditioned> CODEC = (Codec<Conditioned>) ConditionedSelector.CODEC;

    static Selector register(Identifier identifier, Selector selector) {
        return SelectorTypes.register(identifier, selector, null);
    }

    static Selector register(Identifier identifier, Selector selector, Consumer<MacroBuilder> extractors) {
        return SelectorTypes.register(identifier, selector, extractors);
    }

    @Nullable ServerCommandSource select(LootContext context);

    interface Conditioned {
        Optional<ServerCommandSource> select(EventContext context);
    }
}
