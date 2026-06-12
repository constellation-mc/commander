package dev.zenfyr.commander.impl.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.nbt.*;

public class NbtCodecs {

  public static final Codec<Tag> ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
      (dynamic) -> dynamic.convert(NbtOps.INSTANCE).getValue(),
      (element) -> new Dynamic<>(NbtOps.INSTANCE, element));

  public static final Codec<Tag> PRIMITIVE_CODEC = ELEMENT_CODEC.comapFlatMap(
      element -> {
        if (element instanceof NumericTag || element instanceof StringTag)
          return DataResult.success(element);
        return DataResult.error(() -> "Not an nbt primitive %s!".formatted(element));
      },
      Function.identity());

  public static final Codec<CompoundTag> COMPOUND_CODEC = ELEMENT_CODEC.comapFlatMap(
      element -> {
        if (!(element instanceof CompoundTag compound))
          return DataResult.error(() -> "Not an NbtCompound %s!".formatted(element));
        return DataResult.success(compound);
      },
      Function.identity());
}
