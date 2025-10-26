package dev.behindthescenery.sdmrecipemachinestages.compat.info;

import net.minecraft.resources.ResourceLocation;

public interface InfoContext {

    public boolean hideRecipe(String recipeId);

    ResourceLocation getRecipeType();
}
