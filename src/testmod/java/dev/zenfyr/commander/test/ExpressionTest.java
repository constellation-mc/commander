package dev.zenfyr.commander.test;

import com.ezylang.evalex.data.EvaluationValue;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.pulsar.api.loot.LootContextBuilder;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import org.assertj.core.api.Assertions;

public class ExpressionTest {

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testNewOperators(GameTestHelper context) {
    var lootContext = emptyContext(context);

    Map<String, EvaluationValue> map =
        Map.of("x", EvaluationValue.numberValue(23), "y", EvaluationValue.stringValue("Hello"));

    //    Assertions.assertThat(
    //            parse("struct?.x ? 42").eval(lootContext, Map.of("struct",
    // StructureValue.of(map))))
    //        .isEqualTo(Expression.Result.convert(BigDecimal.valueOf(23)));
    //
    //    Assertions.assertThat(
    //            parse("struct?.u").eval(lootContext, Map.of("struct", StructureValue.of(map))))
    //        .matches(Expression.Result::isNullValue, "result is null");

    Assertions.assertThat(parse("null ? 42").eval(lootContext, Map.of("struct", (Expression.Result)
            (Object) EvaluationValue.structureValue(map))))
        .isEqualTo(Expression.Result.convert(42));

    //    Assertions.assertThat(
    //            parse("struct?.u ? 42").eval(lootContext, Map.of("struct",
    // StructureValue.of(map))))
    //        .isEqualTo(Expression.Result.convert(BigDecimal.valueOf(42)));

    context.succeed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testLootContextAccess(GameTestHelper context) {
    var lootContext = emptyContext(context);

    Assertions.assertThat(parse("level.dimension").eval(lootContext))
        .isEqualTo(Expression.Result.convert("minecraft:overworld"));

    context.succeed();
  }

  public static LootContext emptyContext(GameTestHelper context) {
    return LootContextBuilder.empty(context.getLevel());
  }

  public static Expression parse(String expression) {
    var result = Expression.parse(expression);
    if (result.error().isPresent())
      throw new RuntimeException(result.error().get().message());
    return result.result().orElseThrow();
  }
}
