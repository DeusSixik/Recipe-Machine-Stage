package dev.behindthescenery.sdmrecipemachinestages.mixin.jei;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.recipes.collect.RecipeTypeData;
import mezz.jei.library.recipes.collect.RecipeTypeDataMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = RecipeTypeDataMap.class)
public interface RecipeTypeDataMapAccessor {

    @Accessor
    Map<RecipeType<?>, RecipeTypeData<?>> getUidMap();
}
