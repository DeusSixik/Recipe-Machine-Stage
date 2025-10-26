package dev.behindthescenery.sdmrecipemachinestages.compat.jei;

import dev.behindthescenery.sdmrecipemachinestages.exceptions.RecipeTypeUnknownException;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public enum JeiRecipeType {
    Holder,
    Recipe;

    /**
     * Since there can be different Recipe containers in the recipeType Jei in place of the Vanilla RecipeHolder.
     * We will check with our hands what kind of type it is and apply logic from this
     * @param recipeType RecipeClass
     * @return The matched type
     */
    public static JeiRecipeType getRecipeTypeEnum(Class<?> recipeType) {

        if(Recipe.class.isAssignableFrom(recipeType))
            return Recipe;

        if(RecipeHolder.class.isAssignableFrom(recipeType))
            return Holder;

        throw new RecipeTypeUnknownException(recipeType);
    }
}
