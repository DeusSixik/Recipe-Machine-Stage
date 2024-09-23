package net.sdm.recipemachinestage.mixin.jei;

import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.recipes.RecipeManagerInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipeManagerInternal.class, remap = false)
public interface RecipeManagerInternalAccessor {

    @Accessor
    List<IRecipeCategory<?>> getRecipeCategories();
}
