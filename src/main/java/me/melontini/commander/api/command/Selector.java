package me.melontini.commander.api.command;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Function;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.impl.command.ConditionedSelector;
import me.melontini.commander.impl.event.data.types.SelectorTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Base selector function. Selectors transform input {@link LootContext} parameters into {@link ServerCommandSource}.
 * <p>Selectors must be registered with {@link Selector#register(Identifier, Selector)}</p>
 */
public interface Selector extends Function<LootContext, ServerCommandSource> {

  Codec<Conditioned> CODEC = (Codec<Conditioned>) ConditionedSelector.CODEC;

  /**
   * Registers a selector to be used with {@link Command}.
   * @param identifier The selector identifier.
   * @param selector Selector to be registered.
   * @return the provided selector instance.
   */
  static Selector register(Identifier identifier, Selector selector) {
    return SelectorTypes.register(identifier, selector);
  }

  @Override
  default @Nullable ServerCommandSource apply(LootContext context) {
    return this.select(context);
  }

  /**
   * Selects a {@link ServerCommandSource} based on the provided {@link LootContext}.
   * @return {@link ServerCommandSource} extracted from the context or null.
   */
  @Nullable ServerCommandSource select(LootContext context);

  /**
   * Executable selector proxy.
   */
  interface Conditioned {
    /**
     * @see Selector#select(LootContext)
     */
    Optional<ServerCommandSource> select(EventContext context);
  }
}
