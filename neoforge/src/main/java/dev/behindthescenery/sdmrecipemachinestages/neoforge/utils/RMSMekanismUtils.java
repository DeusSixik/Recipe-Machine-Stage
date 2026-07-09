package dev.behindthescenery.sdmrecipemachinestages.neoforge.utils;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSRecipeUtils;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public class RMSMekanismUtils {

    @Nullable
    public static <RECIPE extends Recipe<?>> CacheRecipe<RECIPE> createCache(RECIPE recipe) {
        final RMSRecipeUtils.RecipeValue<?> recipeData = RMSRecipeUtils.getRecipeData(recipe);
        if(recipeData == null)
            return null;

        return new CacheRecipe<>(recipe, recipeData);
    }

    public record CacheRecipe<RECIPE extends Recipe<?>>(RECIPE recipe, RMSRecipeUtils.RecipeValue<?> recipeValue) {

    }
}
