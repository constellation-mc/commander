package me.melontini.commander.api.expression;

import java.util.Map;
import me.melontini.commander.impl.expression.library.ExpressionLibraryLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.UnmodifiableView;

public interface ExpressionLibrary {

  static ExpressionLibrary get(MinecraftServer server) {
    return server.dm$getReloader(ExpressionLibraryLoader.RELOADER);
  }

  Expression getExpression(Identifier id);

  @UnmodifiableView
  Map<Identifier, Expression> allExpressions();
}
