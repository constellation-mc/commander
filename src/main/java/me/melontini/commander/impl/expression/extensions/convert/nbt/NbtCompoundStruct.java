package me.melontini.commander.impl.expression.extensions.convert.nbt;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.extensions.AbstractProxyMap;
import me.melontini.commander.impl.mixin.NbtCompoundAccessor;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
public final class NbtCompoundStruct extends AbstractProxyMap {

  private final NbtCompound compound;
  private Map<String, Expression.Result> view;

  public NbtCompoundStruct(NbtCompound compound) {
    this.compound = compound;
  }

  @Override
  public boolean containsKey(Object o) {
    if (!(o instanceof String key)) return false;
    return compound.contains(key);
  }

  @Override
  public Expression.Result get(Object o) {
    if (!(o instanceof String key)) return null;
    return Expression.Result.convert(compound.get(key));
  }

  @Synchronized
  @NotNull @Override
  public Set<Entry<String, Expression.Result>> entrySet() {
    if (this.view == null) {
      this.view = Maps.transformValues(
          ((NbtCompoundAccessor) compound).commander$toMap(), Expression.Result::convert);
    }
    return this.view.entrySet();
  }

  @Override
  public int size() {
    return compound.getSize();
  }

  @Override
  public String toString() {
    return String.valueOf(compound);
  }
}
