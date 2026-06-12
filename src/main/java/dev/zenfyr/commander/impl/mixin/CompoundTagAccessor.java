package dev.zenfyr.commander.impl.mixin;

import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CompoundTag.class)
public interface CompoundTagAccessor {

  @Invoker("entries")
  Map<String, Tag> commander$toMap();
}
