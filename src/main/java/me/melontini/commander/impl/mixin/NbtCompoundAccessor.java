package me.melontini.commander.impl.mixin;

import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NbtCompound.class)
public interface NbtCompoundAccessor {

  @Invoker("toMap")
  Map<String, NbtElement> commander$toMap();
}
