package me.melontini.commander.impl.expression.extensions;

import me.melontini.commander.api.expression.CustomFieldTransforms;
import me.melontini.commander.impl.expression.extensions.convert.states.StateStruct;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.State;

class CustomFields {

    static void init() {
        CustomFieldTransforms.register(ItemStack.class, "nbt", object -> {
            if (object.getNbt() == null) return new NbtCompound();
            return object.getNbt();
        });
        CustomFieldTransforms.register(Entity.class, "nbt", object -> {
            NbtCompound compound = new NbtCompound();
            object.writeNbt(compound);
            return compound;
        });
        CustomFieldTransforms.register(BlockEntity.class, "nbt", BlockEntity::createNbtWithIdentifyingData);

        CustomFieldTransforms.register(State.class, "properties", StateStruct::new);
    }
}
