package net.sdm.recipemachinestage.mixin.jei;

import mezz.jei.library.recipes.RecipeManager;
import mezz.jei.library.recipes.RecipeManagerInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeManager.class, remap = false)
public interface RecipeManagerAccessor {

    @Accessor
    RecipeManagerInternal getInternal();
}
