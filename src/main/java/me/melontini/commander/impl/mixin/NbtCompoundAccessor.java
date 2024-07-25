package me.melontini.commander.impl.mixin;

import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtCompound.class)
public interface NbtCompoundAccessor {

  @Accessor("entries")
  Map<String, NbtElement> commander$toMap();
}
