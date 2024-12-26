package net.sdm.recipemachinestage.api;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public interface ICraftingTablePath {

   void sdm$uppdate(ItemStack item, String stage);
   void sdm$uppdate(ITableRecipe recipe, Container matrix, String stage);
   void sdm$uppdate(String stage);
    Inventory getRecipe_machine_stage$playerInventory();
    String getSdm$thisStage();
    void setSdm$thisStage(String sdm$thisStage);
}
