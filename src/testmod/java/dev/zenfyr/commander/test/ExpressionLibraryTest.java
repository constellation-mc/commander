package dev.zenfyr.commander.test;

import dev.zenfyr.commander.api.expression.Expression;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import org.assertj.core.api.Assertions;

public class ExpressionLibraryTest {

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testLibraryInExpressions(GameTestHelper context) {
    context
        .startSequence()
        .thenExecute(() -> context.getLevel().setDayTime(1000))
        .thenExecuteAfter(5, () -> {
          var r =
              ExpressionTest.parse("library.cmd:is_day").eval(ExpressionTest.emptyContext(context));
          Assertions.assertThat(r).isEqualTo(Expression.Result.convert(true));
        })
        .thenSucceed();
  }
}
