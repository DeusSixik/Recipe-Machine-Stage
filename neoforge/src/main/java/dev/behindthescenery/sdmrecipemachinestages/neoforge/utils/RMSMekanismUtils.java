package dev.behindthescenery.sdmrecipemachinestages.neoforge.utils;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSRecipeUtils;
import net.minecraft.world.item.crafting.Recipe;

public class RMSMekanismUtils {

    public static <RECIPE extends Recipe<?>> CacheRecipe<RECIPE> createCache(RECIPE recipe) {
        final RMSRecipeUtils.RecipeValue<?> recipeData = RMSRecipeUtils.getRecipeData(recipe);
        if(recipeData == null)
            throw new NullPointerException("Can't find recipe Data");

        return new CacheRecipe<>(recipe, recipeData);
    }

    public record CacheRecipe<RECIPE extends Recipe<?>>(RECIPE recipe, RMSRecipeUtils.RecipeValue<?> recipeValue) {

    }
}
