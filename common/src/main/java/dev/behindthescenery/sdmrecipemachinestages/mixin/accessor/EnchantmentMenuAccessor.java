package dev.behindthescenery.sdmrecipemachinestages.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentMenu.class)
public interface EnchantmentMenuAccessor {

    @Accessor
    ContainerLevelAccess getAccess();

    @Accessor
    Container getEnchantSlots();

    @Accessor
    DataSlot getEnchantmentSeed();
}
