package dev.zenfyr.commander.impl.expression.extension;

import com.mojang.serialization.MapCodec;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.impl.Commander;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record ExpressionLootNumberProvider(Expression value) implements NumberProvider {

  public static final MapCodec<ExpressionLootNumberProvider> CODEC = Expression.CODEC
      .xmap(ExpressionLootNumberProvider::new, ExpressionLootNumberProvider::value)
      .fieldOf("value");

  @Override
  public LootNumberProviderType getType() {
    return Commander.EXPRESSION_PROVIDER;
  }

  @Override
  public float getFloat(LootContext context) {
    return this.value.eval(context).getAsFloat();
  }

  public static ExpressionLootNumberProvider create(Expression value) {
    return new ExpressionLootNumberProvider(value);
  }
}
