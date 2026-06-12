package dev.zenfyr.commander.test;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.number.NumberUtils;
import com.ezylang.evalex.data.number.NumberValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.zenfyr.commander.impl.expression.conversion.ReflectiveValueConverter;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.assertj.core.api.Assertions;

public class ConvertersTest {

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testOptionalConvert(GameTestHelper context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(Optional.empty()))
        .isEqualTo(EvaluationValue.NULL_VALUE);

    Assertions.assertThat(ReflectiveValueConverter.convert(Optional.of(123)))
        .isEqualTo(EvaluationValue.numberValue(123));

    context.succeed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testIdentifierConvert(GameTestHelper context) {
    Assertions.assertThat(
            ReflectiveValueConverter.convert(ResourceLocation.tryBuild("cmd", "test_id")))
        .isEqualTo(EvaluationValue.stringValue("cmd:test_id"));

    Assertions.assertThat(ReflectiveValueConverter.convert(new ResourceLocation("test_id")))
        .isEqualTo(EvaluationValue.stringValue("minecraft:test_id"));

    context.succeed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testNbtConvert(GameTestHelper context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(new CompoundTag()))
        .isInstanceOf(EvaluationValue.class)
        .extracting(EvaluationValue::getDataType)
        .isEqualTo(EvaluationValue.DataType.STRUCTURE);

    Assertions.assertThat(ReflectiveValueConverter.convert(new ListTag()))
        .isInstanceOf(EvaluationValue.class)
        .extracting(EvaluationValue::getDataType)
        .isEqualTo(EvaluationValue.DataType.ARRAY);

    Assertions.assertThat(ReflectiveValueConverter.convert(StringTag.valueOf("Hello World!")))
        .isEqualTo(EvaluationValue.stringValue("Hello World!"));

    Assertions.assertThat(ReflectiveValueConverter.convert(IntTag.valueOf(123)))
        .isEqualTo(EvaluationValue.numberValue(123));

    Assertions.assertThat(ReflectiveValueConverter.convert(DoubleTag.valueOf(123.4587)))
        .isEqualTo(EvaluationValue.numberValue(123.4587));

    context.succeed();
  }

  @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
  public void testGsonConvert(GameTestHelper context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonObject()))
        .isInstanceOf(EvaluationValue.class)
        .extracting(EvaluationValue::getDataType)
        .isEqualTo(EvaluationValue.DataType.STRUCTURE);

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonArray()))
        .isInstanceOf(EvaluationValue.class)
        .extracting(EvaluationValue::getDataType)
        .isEqualTo(EvaluationValue.DataType.ARRAY);

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive("Hello World!")))
        .isEqualTo(EvaluationValue.stringValue("Hello World!"));

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive(123)))
        .extracting(EvaluationValue::getNumberValue)
        .usingComparator(NumberValue::compareTo)
        .isEqualTo(NumberUtils.of(123));

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive(123.4587)))
        .extracting(EvaluationValue::getNumberValue)
        .usingComparator(NumberValue::compareTo)
        .isEqualTo(NumberUtils.of(123.4587));

    context.succeed();
  }
}
