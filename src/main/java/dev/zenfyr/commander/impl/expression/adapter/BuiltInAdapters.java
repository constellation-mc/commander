package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.config.ConstantCollection;
import com.ezylang.evalex.data.adapter.TypeAdapterRegistry;
import com.ezylang.evalex.data.adapter.basic.ImmutableMapAdapter;
import com.ezylang.evalex.data.adapter.basic.MapAdapter;
import com.google.common.collect.ImmutableMap;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.commander.impl.expression.adapter.core.ListAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.PositionAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.StringAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.Vec3iAdapter;
import dev.zenfyr.commander.impl.expression.adapter.entity.EntityAdapter;
import dev.zenfyr.commander.impl.expression.adapter.entity.LivingEntityAdapter;
import dev.zenfyr.commander.impl.expression.adapter.level.ChunkAccessAdapter;
import dev.zenfyr.commander.impl.expression.adapter.level.LevelAdapter;
import dev.zenfyr.commander.impl.expression.adapter.level.MinecraftServerAdapter;
import dev.zenfyr.commander.impl.expression.adapter.level.ServerLevelAdapter;
import dev.zenfyr.commander.impl.expression.adapter.state.BlockStateBaseAdapter;
import dev.zenfyr.commander.impl.expression.adapter.state.FluidStateAdapter;
import dev.zenfyr.commander.impl.expression.adapter.state.StateHolderAdapter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.text.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;

public class BuiltInAdapters {

  public static TypeAdapterRegistry init() {
    ImmutableMapAdapter ccAdapter = new ImmutableMapAdapter();

    var builder = TypeAdapterRegistry.builder()
        .addAdapter(Map.class, new MapAdapter())
        .addAdapter(ConstantCollection.class, ccAdapter)
        .addAdapter(ImmutableMap.class, ccAdapter);

    try {
      var cls = Class.forName("java.util.ImmutableCollections$AbstractImmutableMap");
      builder.addAdapter(cls, ccAdapter);
    } catch (ClassNotFoundException e) {
      Commander.LOGGER.warn("Failed to look up internal ImmutableCollections class.", e);
    }

    return builder
        // basic
        .addAdapter(List.class, new ListAdapter())
        .addAdapter(String.class, new StringAdapter())
        // expression lib
        .addAdapter(ExpressionLibrary.class, new ExpressionLibraryAdapter())
        // core
        .addAdapter(Position.class, new PositionAdapter<>())
        .addAdapter(Vec3i.class, new Vec3iAdapter<>())
        // level
        .addAdapter(MinecraftServer.class, new MinecraftServerAdapter())
        .addAdapter(Level.class, new LevelAdapter<>())
        .addAdapter(ServerLevel.class, new ServerLevelAdapter())
        .addAdapter(ChunkAccess.class, new ChunkAccessAdapter())
        // state
        .addAdapter(StateHolder.class, new StateHolderAdapter<>())
        .addAdapter(BlockState.class, new BlockStateBaseAdapter())
        .addAdapter(FluidState.class, new FluidStateAdapter())
        // stack
        .addAdapter(ItemStack.class, new ItemStackAdapter())
        // block entity
        .addAdapter(BlockEntity.class, new BlockEntityAdapter())
        // entity
        .addAdapter(Entity.class, new EntityAdapter<>())
        .addAdapter(LivingEntity.class, new LivingEntityAdapter<>())
        .addAdapter(AttributeMap.class, new AttributeMapAdapter())
        // damage source
        .addAdapter(DamageSource.class, new DamageSourceAdapter())
        .build();
  }
}
