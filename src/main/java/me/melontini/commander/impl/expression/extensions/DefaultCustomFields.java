package me.melontini.commander.impl.expression.extensions;

import me.melontini.commander.api.expression.extensions.CustomFields;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.extensions.convert.RegistryAccessStruct;
import me.melontini.commander.impl.expression.extensions.convert.attributes.EntityAttributesStruct;
import me.melontini.commander.impl.expression.extensions.convert.components.ComponentStruct;
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
      if (object.isEmpty()) return EMPTY;
      return object.encode(Commander.get().currentServer().getRegistryManager());
    });
    CustomFields.addVirtualField(Entity.class, "nbt", NbtPredicate::entityToNbt);
    CustomFields.addVirtualField(
        BlockEntity.class,
        "nbt",
        be -> be.createNbtWithIdentifyingData(be.getWorld().getRegistryManager()));

    CustomFields.addVirtualField(State.class, "properties", StateStruct::new);
    CustomFields.addVirtualField(
        LivingEntity.class, "attributes", e -> new EntityAttributesStruct(e.getAttributes()));
    CustomFields.addVirtualField(
        ItemStack.class, "components", stack -> new ComponentStruct(stack.getComponents()));

    CustomFields.addVirtualField(
        AttachmentTarget.class,
        "storage",
        target -> target.getAttachedOrCreate(Commander.DATA_ATTACHMENT));
    CustomFields.addVirtualField(Registry.class, "access", RegistryAccessStruct::forRegistry);
  }
}
