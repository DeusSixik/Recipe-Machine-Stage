package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.minecraft;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin {

    @Shadow
    private List<RecipeHolder<StonecutterRecipe>> recipes;

    @Shadow
    @Final
    private DataSlot selectedRecipeIndex;

    @Shadow
    @Final
    private Slot resultSlot;

    @Shadow
    @Final
    private Level level;

    @Unique
    private Inventory bts$inventory;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    public void bts$init(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
        this.bts$inventory = inventory;
    }


    @Redirect(method = "setupRecipeList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> setupRecipeList(RecipeManager instance, RecipeType<T> recipeType, I recipeInput, Level level) {
        return RMSUtils.filterRecipes(instance.getRecipesFor(recipeType, recipeInput, level), bts$inventory.player);
    }
}
