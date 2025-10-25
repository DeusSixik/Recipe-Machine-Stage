package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.irons_spell_books;

import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArcaneAnvilMenu.class)
public abstract class ArcaneAnvilMenuMixin extends ItemCombinerMenu {

    protected ArcaneAnvilMenuMixin(@Nullable MenuType<?> arg, int i, Inventory arg2, ContainerLevelAccess arg3) {
        super(arg, i, arg2, arg3);
    }
}
