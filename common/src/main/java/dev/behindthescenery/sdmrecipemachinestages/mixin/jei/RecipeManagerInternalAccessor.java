package dev.behindthescenery.sdmrecipemachinestages.mixin.jei;

import mezz.jei.library.recipes.RecipeManagerInternal;
import mezz.jei.library.recipes.collect.RecipeTypeDataMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeManagerInternal.class)
public interface RecipeManagerInternalAccessor {

    @Accessor
    RecipeTypeDataMap getRecipeTypeDataMap();
}
