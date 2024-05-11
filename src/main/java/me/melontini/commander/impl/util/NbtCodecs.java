package me.melontini.commander.impl.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;

import java.util.function.Function;

public class NbtCodecs {

    public static final Codec<NbtElement> ELEMENT_CODEC = Codec.PASSTHROUGH.xmap((dynamic) -> dynamic.convert(NbtOps.INSTANCE).getValue(), (element) -> new Dynamic<>(NbtOps.INSTANCE, element));

    public static final Codec<NbtElement> PRIMITIVE_CODEC = ELEMENT_CODEC.comapFlatMap(element -> {
        if (element instanceof AbstractNbtNumber || element instanceof NbtString) return DataResult.success(element);
        return DataResult.error(() -> "Not an nbt primitive %s!".formatted(element));
    }, Function.identity());

    public static final Codec<NbtCompound> COMPOUND_CODEC = ELEMENT_CODEC.comapFlatMap(element -> {
        if (!(element instanceof NbtCompound compound)) return DataResult.error(() -> "Not an NbtCompound %s!".formatted(element));
        return DataResult.success(compound);
    }, Function.identity());
}
