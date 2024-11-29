package net.sdm.recipemachinestage.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.sdm.recipemachinestage.compat.extendercrafting.IWrappedRecipeAddition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AutoTableTileEntity.WrappedRecipe.class)
public class WrappedRecipeMixin implements IWrappedRecipeAddition {

    @Unique
    private Recipe<?> recipe_machine_stage$recipe;

    @Inject(method = "<init>(Lnet/minecraft/world/item/crafting/CraftingRecipe;Lnet/minecraft/world/inventory/CraftingContainer;)V", at = @At("RETURN"))
    public void sdm$const(CraftingRecipe recipe, CraftingContainer craftingInventory, CallbackInfo ci) {
        this.recipe_machine_stage$recipe = recipe;
    }
    @Inject(method = "<init>(Lcom/blakebr0/extendedcrafting/api/crafting/ITableRecipe;)V", at = @At("RETURN"))
    public void sdm$const(ITableRecipe recipe, CallbackInfo ci) {
        this.recipe_machine_stage$recipe = recipe;
    }

    @Override
    public Recipe<?> recipe_machine_stage$getRecipe() {
        return recipe_machine_stage$recipe;
    }
}
