package net.sdm.recipemachinestage.compat.thermal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public interface IThermalRecipeAddition {

    ResourceLocation getRecipeID();
    void setRecipeID(ResourceLocation recipeID);
    RecipeType<?> getRecipeType();
    void setRecipeType(RecipeType<?> recipeType);
}
