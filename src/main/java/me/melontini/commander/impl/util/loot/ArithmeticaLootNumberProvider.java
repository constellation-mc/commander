package me.melontini.commander.impl.util.loot;

import me.melontini.commander.api.expression.Arithmetica;
import me.melontini.commander.impl.Commander;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;

public record ArithmeticaLootNumberProvider(Arithmetica value) implements LootNumberProvider {

    @Override
    public LootNumberProviderType getType() {
        return Commander.ARITHMETICA_PROVIDER;
    }

    @Override
    public float nextFloat(LootContext context) {
        return this.value.asFloat(context);
    }

    public static ArithmeticaLootNumberProvider create(Arithmetica value) {
        return new ArithmeticaLootNumberProvider(value);
    }
}
