package me.melontini.commander.api.expression;

import java.util.Map;
import me.melontini.commander.impl.expression.library.ExpressionLibraryLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Provides access to user defined expression library.
 */
@ApiStatus.NonExtendable
public interface ExpressionLibrary {

  static ExpressionLibrary get(MinecraftServer server) {
    return server.dm$getReloader(ExpressionLibraryLoader.RELOADER);
  }

  /**
   * Internally used by the {@code library} container and brigadier commands.
   * @param id The expression identifier.
   * @return {@link Expression} with the following ID or null if not present.
   */
  @Nullable Expression getExpression(Identifier id);

  /**
   * Internally used to append command suggestions.
   * @return All expressions in the user library.
   */
  @UnmodifiableView
  Map<Identifier, Expression> allExpressions();
}
