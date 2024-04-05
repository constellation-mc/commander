package me.melontini.commander.api.command;

import com.mojang.serialization.Codec;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.command.ConditionedSelector;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Selector {

    Codec<Conditioned> CODEC = (Codec<Conditioned>) ConditionedSelector.CODEC;

    static Selector register(Identifier identifier, Selector selector) {
        return SelectorTypes.register(identifier, selector);
    }

    @Nullable ServerCommandSource select(LootContext context);

    interface Conditioned {
        Optional<ServerCommandSource> select(EventContext context);
    }
}
