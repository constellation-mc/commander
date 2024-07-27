package me.melontini.commander.test;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.extensions.convert.nbt.NbtCompoundStruct;
import me.melontini.handytests.server.ServerTestContext;
import me.melontini.handytests.server.ServerTestEntrypoint;
import me.melontini.handytests.util.runner.HandyTest;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import org.assertj.core.api.Assertions;

public class ConvertersTest implements ServerTestEntrypoint {

  @HandyTest
  void testOptionalConvert(ServerTestContext context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(Optional.empty()))
        .isEqualTo(NullValue.of());

    Assertions.assertThat(ReflectiveValueConverter.convert(Optional.of(123)))
        .isEqualTo(NumberValue.of(BigDecimal.valueOf(123)));
  }

  @HandyTest
  void testIdentifierConvert(ServerTestContext context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(Identifier.of("cmd", "test_id")))
        .isEqualTo(StringValue.of("cmd:test_id"));

    Assertions.assertThat(ReflectiveValueConverter.convert(Identifier.of("test_id")))
        .isEqualTo(StringValue.of("minecraft:test_id"));
  }

  @HandyTest
  void testNbtConvert(ServerTestContext context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(new NbtCompound()))
        .isInstanceOf(StructureValue.class)
        .isEqualTo(StructureValue.of(
            (Map<String, EvaluationValue>) (Object) new NbtCompoundStruct(new NbtCompound())));

    Assertions.assertThat(ReflectiveValueConverter.convert(new NbtList()))
        .isInstanceOf(ArrayValue.class);

    Assertions.assertThat(ReflectiveValueConverter.convert(NbtString.of("Hello World!")))
        .isEqualTo(StringValue.of("Hello World!"));

    Assertions.assertThat(ReflectiveValueConverter.convert(NbtInt.of(123)))
        .isEqualTo(NumberValue.of(BigDecimal.valueOf(123)));

    Assertions.assertThat(ReflectiveValueConverter.convert(NbtDouble.of(123.4587)))
        .isEqualTo(NumberValue.of(BigDecimal.valueOf(123.4587)));
  }

  @HandyTest
  void testGsonConvert(ServerTestContext context) {
    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonObject()))
        .isInstanceOf(StructureValue.class);

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonArray()))
        .isInstanceOf(ArrayValue.class);

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive("Hello World!")))
        .isEqualTo(StringValue.of("Hello World!"));

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive(123)))
        .isEqualTo(NumberValue.of(BigDecimal.valueOf(123)));

    Assertions.assertThat(ReflectiveValueConverter.convert(new JsonPrimitive(123.4587)))
        .isEqualTo(NumberValue.of(BigDecimal.valueOf(123.4587)));
  }
}
