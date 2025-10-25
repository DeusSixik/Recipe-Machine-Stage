package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.jumbo_furnace;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.commoble.jumbofurnace.JumboFurnace;
import net.commoble.jumbofurnace.jumbo_furnace.JumboFurnaceCoreBlockEntity;
import net.commoble.jumbofurnace.recipes.JumboFurnaceRecipe;
import net.commoble.jumbofurnace.recipes.RecipeSorter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;

@Mixin(JumboFurnaceCoreBlockEntity.class)
public abstract class JumboFurnaceCoreBlockEntityMixin extends BlockEntity {

    protected JumboFurnaceCoreBlockEntityMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Redirect(method = "processInputs", at = @At(value = "INVOKE", target = "Lnet/commoble/jumbofurnace/recipes/RecipeSorter;getSortedFurnaceRecipesValidForInputs(Ljava/util/Collection;Lnet/minecraft/world/item/crafting/RecipeManager;)Ljava/util/SortedSet;"))
    public SortedSet<JumboFurnaceRecipe> bts$processInputs(RecipeSorter instance, Collection<Item> item, RecipeManager recipeManager) {
        final List<RecipeHolder<JumboFurnaceRecipe>> recipes = RMSUtils.filterRecipes(recipeManager.getAllRecipesFor(JumboFurnace.get().jumboSmeltingRecipeType.get()), RMSUtils.getBlockOwner(this));

        final SortedSet<JumboFurnaceRecipe> recipesForItems = new ObjectRBTreeSet<>(RecipeSorter::compareRecipes);
        for (final RecipeHolder<JumboFurnaceRecipe> holder : recipes) {
            final JumboFurnaceRecipe recipe = holder.value();

            bo:
            for(final Ingredient ingredient : recipe.getIngredients()) {
                for(final ItemStack stack : ingredient.getItems()) {

                    if(item.contains(stack.getItem())) {
                        recipesForItems.add(recipe);
                        break bo;
                    }
                }
            }
        }

        final List<RecipeHolder<SmeltingRecipe>> recipes2 = RMSUtils.filterRecipes(recipeManager.getAllRecipesFor(RecipeType.SMELTING), RMSUtils.getBlockOwner(this));
        for (final RecipeHolder<SmeltingRecipe> holder : recipes2) {
            final SmeltingRecipe recipe = holder.value();

            bo:
            for(final Ingredient ingredient : recipe.getIngredients()) {
                for(final ItemStack stack : ingredient.getItems()) {

                    if(item.contains(stack.getItem())) {
                        recipesForItems.add(new JumboFurnaceRecipe(recipe));
                        break bo;
                    }
                }
            }
        }

        return recipesForItems;
    }
}
