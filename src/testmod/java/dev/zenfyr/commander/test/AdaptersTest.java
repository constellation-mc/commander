package dev.zenfyr.commander.test;

import dev.zenfyr.commander.api.expression.Expression;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import org.assertj.core.api.Assertions;

public class AdaptersTest {

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testLevelAdapter(GameTestHelper context) {
    context
        .startSequence()
        .thenExecute(() -> context.getLevel().setDayTime(1000))
        .thenExecuteAfter(1, () -> {
          var r = Expression.parse("level.isDay()")
              .result()
              .orElseThrow()
              .eval(ExpressionTest.emptyContext(context));
          Assertions.assertThat(r).isEqualTo(Expression.Result.convert(true));
        })
        .thenSucceed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testBlockAdapter(GameTestHelper context) {
    context
        .startSequence()
        .thenExecute(() -> context.setBlock(0, 1, 0, Blocks.GRASS_BLOCK))
        .thenExecuteAfter(1, () -> {
          var abs = context.absolutePos(new BlockPos(0, 1, 0));
          var r = Expression.parse(
                  "level.block(%s, %s, %s).key".formatted(abs.getX(), abs.getY(), abs.getZ()))
              .result()
              .orElseThrow()
              .eval(ExpressionTest.emptyContext(context));
          Assertions.assertThat(r).isEqualTo(Expression.Result.convert("minecraft:grass_block"));
        })
        .thenSucceed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testFluidAdapter(GameTestHelper context) {
    context
        .startSequence()
        .thenExecute(() -> context.setBlock(0, 1, 0, Blocks.WATER))
        .thenExecuteAfter(1, () -> {
          var abs = context.absolutePos(new BlockPos(0, 1, 0));
          var r = Expression.parse(
                  "level.fluid(%s, %s, %s).key".formatted(abs.getX(), abs.getY(), abs.getZ()))
              .result()
              .orElseThrow()
              .eval(ExpressionTest.emptyContext(context));
          Assertions.assertThat(r).isEqualTo(Expression.Result.convert("minecraft:water"));
        })
        .thenSucceed();
  }
}
