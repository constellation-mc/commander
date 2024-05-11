package me.melontini.commander.impl.expression.extensions;

import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.extensions.convert.attributes.EntityAttributesStruct;
import me.melontini.commander.impl.expression.extensions.convert.states.StateStruct;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.State;

class CustomFields {

    static void init() {
        ReflectiveMapStructure.addField(ItemStack.class, "nbt", object -> {
            if (object.getNbt() == null) return new NbtCompound();
            return object.getNbt();
        });
        ReflectiveMapStructure.addField(Entity.class, "nbt", object -> {
            NbtCompound compound = new NbtCompound();
            object.writeNbt(compound);
            return compound;
        });
        ReflectiveMapStructure.addField(BlockEntity.class, "nbt", BlockEntity::createNbtWithIdentifyingData);

        ReflectiveMapStructure.addField(State.class, "properties", StateStruct::new);
        ReflectiveMapStructure.addField(LivingEntity.class, "attributes", e -> new EntityAttributesStruct(e.getAttributes()));

        ReflectiveMapStructure.addField(AttachmentTarget.class, "storage", target -> target.getAttachedOrCreate(Commander.DATA_ATTACHMENT));
    }
}
