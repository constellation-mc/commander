package me.melontini.commander.api.expression;

import me.melontini.commander.impl.expression.library.ExpressionLibraryLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public interface ExpressionLibrary {

  static ExpressionLibrary get(MinecraftServer server) {
    return server.dm$getReloader(ExpressionLibraryLoader.RELOADER);
  }

  Expression getExpression(Identifier id);
}
