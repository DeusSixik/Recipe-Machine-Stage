package dev.behindthescenery.sdmrecipemachinestages.mixin.jei;

import mezz.jei.library.recipes.RecipeManagerInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "mezz.jei.library.recipes.RecipeManager", remap = false)
public interface RecipeManagerAccessor {

    @Accessor(remap = false)
    RecipeManagerInternal getInternal();
}
