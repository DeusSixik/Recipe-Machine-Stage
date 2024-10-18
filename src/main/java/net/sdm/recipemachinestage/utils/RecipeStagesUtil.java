package net.sdm.recipemachinestage.utils;


import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeTypeCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeWrapper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.crafting.Recipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraftforge.fml.ModList;

/**
 * Source
 * https://github.com/jaredlll08/RecipeStages/blob/1.20.1/src/main/java/com/blamejared/recipestages/RecipeStagesUtil.java#L14
 */
public class RecipeStagesUtil {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {

        return (T) o;
    }


    public static<T> RecipeType<T> getRecipeType(Object recipeType, IRecipeCategory<?> category) {
        if(ModList.get().isLoaded("gtceu")) {
            if (recipeType instanceof GTRecipe recipe) {
                return (RecipeType<T>) GTRecipeTypeCategory.TYPES.apply(recipe.recipeType);
            }
        }
        return (RecipeType<T>) category.getRecipeType();
    }

    public static Object getRecipe(Object recipe) {
        if(ModList.get().isLoaded("gtceu")) {
            if(recipe instanceof GTRecipe gtRecipe) {
                return new GTRecipeWrapper(gtRecipe);
            }
        }

        return  (Recipe<?>)cast(recipe);
    }

    public static boolean isCorrectRecipeClass(IRecipeCategory<?> category, Recipe<?> recipe) {
        if(ReflectionHelper.canCast(category.getRecipeType().getRecipeClass(), recipe.getClass())) return true;

        if(ModList.get().isLoaded("gtceu")) {
            if(recipe instanceof GTRecipe gtRecipe && category instanceof GTRecipeTypeCategory hCategory) {
                GTRecipeWrapper wrapper = new GTRecipeWrapper(gtRecipe);
                return ReflectionHelper.canCast(hCategory.getRecipeType().getRecipeClass(), wrapper.getClass());
            }
        }

        return false;
    }
}
