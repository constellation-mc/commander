package dev.zenfyr.commander.api.expression;

import dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Provides access to user defined expression library.
 */
@ApiStatus.NonExtendable
public interface ExpressionLibrary {

  static ExpressionLibrary get(MinecraftServer server) {
    return server.pulsar$getReloadListener(ExpressionLibraryLoader.RELOADER);
  }

  /**
   * Internally used by the {@code library} container and brigadier commands.
   * @param id The expression identifier.
   * @return {@link Expression} with the following ID or null if not present.
   */
  @Nullable Expression getExpression(ResourceLocation id);

  @Nullable Expression getExpression(String id);

  /**
   * Internally used to append command suggestions.
   * @return All expressions in the user library.
   */
  @UnmodifiableView
  Map<ResourceLocation, Expression> allExpressions();
}
