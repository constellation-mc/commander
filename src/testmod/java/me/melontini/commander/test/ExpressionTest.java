package me.melontini.commander.test;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.ezylang.evalex.data.types.StringValue;
import com.ezylang.evalex.data.types.StructureValue;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import me.melontini.commander.api.expression.Expression;
import me.melontini.handytests.server.ServerTestContext;
import me.melontini.handytests.server.ServerTestEntrypoint;
import me.melontini.handytests.util.runner.HandyTest;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import org.assertj.core.api.Assertions;

public class ExpressionTest implements ServerTestEntrypoint {

  @HandyTest
  void testNewOperators(ServerTestContext context) {
    var lootContext = emptyContext(context);

    Map<String, EvaluationValue> map =
        Map.of("x", NumberValue.of(BigDecimal.valueOf(23)), "y", StringValue.of("Hello"));

    Assertions.assertThat(
            parse("struct?.x ? 42").eval(lootContext, Map.of("struct", StructureValue.of(map))))
        .isEqualTo(Expression.Result.convert(BigDecimal.valueOf(23)));

    Assertions.assertThat(
            parse("struct?.u").eval(lootContext, Map.of("struct", StructureValue.of(map))))
        .matches(Expression.Result::isNullValue, "result is null");

    Assertions.assertThat(
            parse("null ? 42").eval(lootContext, Map.of("struct", StructureValue.of(map))))
        .isEqualTo(Expression.Result.convert(BigDecimal.valueOf(42)));

    Assertions.assertThat(
            parse("struct?.u ? 42").eval(lootContext, Map.of("struct", StructureValue.of(map))))
        .isEqualTo(Expression.Result.convert(BigDecimal.valueOf(42)));
  }

  @HandyTest
  void testLootContextAccess(ServerTestContext context) {
    var lootContext = emptyContext(context);

    Assertions.assertThat(parse("level.dimension.location").eval(lootContext))
        .isEqualTo(Expression.Result.convert("minecraft:overworld"));
  }

  public static LootContext emptyContext(ServerTestContext context) {
    return new LootContext.Builder(
            new LootContextParameterSet.Builder(context.server().getOverworld())
                .build(LootContextTypes.EMPTY))
        .build(Optional.empty());
  }

  public static Expression parse(String expression) {
    var result = Expression.parse(expression);
    if (result.error().isPresent())
      throw new RuntimeException(result.error().get().message());
    return result.result().orElseThrow();
  }
}
