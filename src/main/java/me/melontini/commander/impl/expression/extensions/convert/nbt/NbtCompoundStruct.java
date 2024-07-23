package me.melontini.commander.impl.expression.extensions.convert.nbt;

import lombok.EqualsAndHashCode;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import net.minecraft.nbt.NbtCompound;

@EqualsAndHashCode(callSuper = false)
public class NbtCompoundStruct extends ProxyMap {

  private final NbtCompound compound;

  public NbtCompoundStruct(NbtCompound compound) {
    this.compound = compound;
  }

  @Override
  public boolean containsKey(String key) {
    return compound.contains(key);
  }

  @Override
  public Object getValue(String key) {
    return compound.get(key);
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
