package me.melontini.commander.impl.expression.extensions;

import me.melontini.commander.api.expression.extensions.CustomFields;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.extensions.convert.RegistryAccessStruct;
import me.melontini.commander.impl.expression.extensions.convert.attributes.EntityAttributesStruct;
import me.melontini.commander.impl.expression.extensions.convert.states.StateStruct;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registry;
import net.minecraft.state.State;

class DefaultCustomFields {

    private static final NbtCompound EMPTY = new NbtCompound();

    static void init() {
        CustomFields.addVirtualField(ItemStack.class, "nbt", object -> {
            if (object.getNbt() == null) return EMPTY;
            return object.getNbt();
        });
        CustomFields.addVirtualField(Entity.class, "nbt", NbtPredicate::entityToNbt);
        CustomFields.addVirtualField(BlockEntity.class, "nbt", BlockEntity::createNbtWithIdentifyingData);

        CustomFields.addVirtualField(State.class, "properties", StateStruct::new);
        CustomFields.addVirtualField(LivingEntity.class, "attributes", e -> new EntityAttributesStruct(e.getAttributes()));

        CustomFields.addVirtualField(AttachmentTarget.class, "storage", target -> target.getAttachedOrCreate(Commander.DATA_ATTACHMENT));
        CustomFields.addVirtualField(Registry.class, "access", RegistryAccessStruct::forRegistry);
    }
}
