package me.melontini.commander.test;

import static me.melontini.commander.test.ExpressionTest.emptyContext;
import static me.melontini.commander.test.ExpressionTest.parse;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.BooleanValue;
import com.ezylang.evalex.data.types.DataAccessorValue;
import com.ezylang.evalex.data.types.StructureValue;
import java.util.Map;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.extensions.convert.components.ComponentStruct;
import me.melontini.commander.impl.expression.extensions.convert.nbt.NbtCompoundStruct;
import me.melontini.handytests.server.ServerTestContext;
import me.melontini.handytests.server.ServerTestEntrypoint;
import me.melontini.handytests.util.runner.HandyTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.math.BlockPos;
import org.assertj.core.api.Assertions;

public class VirtualFieldsTest implements ServerTestEntrypoint {

  @HandyTest
  void testStackFields(ServerTestContext context) {
    ItemStack stack = Items.ACACIA_LOG.getDefaultStack();

    var lootContext = emptyContext(context);
    Assertions.assertThat(parse("stack.nbt").eval(lootContext, Map.of("stack", stack)))
        .isInstanceOf(StructureValue.class)
        .isEqualTo(StructureValue.of((Map<String, EvaluationValue>) (Object) new NbtCompoundStruct(
            (NbtCompound) stack.encodeAllowEmpty(context.server().getRegistryManager()))));

    Assertions.assertThat(parse("stack.components").eval(lootContext, Map.of("stack", stack)))
        .isInstanceOf(DataAccessorValue.class)
        .isEqualTo(ReflectiveValueConverter.convert(new ComponentStruct(stack.getComponents())));
  }

  @HandyTest
  void testEntityFields(ServerTestContext context) {
    Entity entity = EntityType.SLIME.create(context.context().getOverworld());

    var lootContext = emptyContext(context);
    Assertions.assertThat(parse("entity.nbt").eval(lootContext, Map.of("entity", entity)))
        .isInstanceOf(StructureValue.class)
        .isEqualTo(StructureValue.of((Map<String, EvaluationValue>)
            (Object) new NbtCompoundStruct(NbtPredicate.entityToNbt(entity))));
  }

  @HandyTest
  void testBlockEntityFields(ServerTestContext context) {
    BlockEntity entity = BlockEntityType.CAMPFIRE.instantiate(
        new BlockPos(100, 60, 100), Blocks.CAMPFIRE.getDefaultState());

    var lootContext = emptyContext(context);
    Assertions.assertThat(parse("be.nbt").eval(lootContext, Map.of("be", entity)))
        .isInstanceOf(StructureValue.class)
        .isEqualTo(StructureValue.of((Map<String, EvaluationValue>) (Object) new NbtCompoundStruct(
            entity.createNbtWithIdentifyingData(context.server().getRegistryManager()))));
  }

  @HandyTest
  void testBlockStateFields(ServerTestContext context) {
    var lootContext = emptyContext(context);

    Assertions.assertThat(parse("state.properties.lit")
            .eval(lootContext, Map.of("state", Blocks.CAMPFIRE.getDefaultState())))
        .isEqualTo(BooleanValue.of(true));
  }
}
