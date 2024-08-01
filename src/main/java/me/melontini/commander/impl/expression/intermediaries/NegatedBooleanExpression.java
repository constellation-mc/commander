package me.melontini.commander.impl.expression.intermediaries;

import com.mojang.datafixers.util.Either;
import me.melontini.commander.api.expression.BooleanExpression;
import net.minecraft.loot.context.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record NegatedBooleanExpression(BooleanExpression delegate) implements BooleanExpression {

    @Override
    public boolean asBoolean(LootContext context, @Nullable Map<String, ?> parameters) {
        return !delegate().asBoolean(context, parameters);
    }

    @Override
    public Either<Boolean, String> toSource() {
        return delegate.toSource();
    }
}
