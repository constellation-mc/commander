package me.melontini.commander.test;

import me.melontini.commander.api.expression.Expression;
import me.melontini.handytests.server.ServerTestContext;
import me.melontini.handytests.server.ServerTestEntrypoint;
import me.melontini.handytests.util.runner.HandyTest;
import org.assertj.core.api.Assertions;

public class ExpressionLibraryTest implements ServerTestEntrypoint {

  @HandyTest
  void testLibraryInExpressions(ServerTestContext context) {
    context
        .server()
        .getCommandManager()
        .executeWithPrefix(context.server().getCommandSource(), "/time set day");

    Assertions.assertThat(
            ExpressionTest.parse("library.cmd:is_day").eval(ExpressionTest.emptyContext(context)))
        .isEqualTo(Expression.Result.convert(true));
  }
}
