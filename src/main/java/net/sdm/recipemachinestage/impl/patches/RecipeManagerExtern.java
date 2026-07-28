package net.sdm.recipemachinestage.impl.patches;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

public interface RecipeManagerExtern {

    static RecipeManagerExtern get(RecipeManager manager) {
        if(manager instanceof RecipeManagerExtern out)
            return out;
        throw new IllegalArgumentException("Need implement RecipeManagerExtern for " + manager.getClass().getName());
    }

    Recipe[] getRecipesRaw();

    int getRecipeIndex(ResourceLocation id);

    Recipe<?> getRecipeByIndex(int index);

    ResourceLocation getRecipeIdByIndex(int index);
}
