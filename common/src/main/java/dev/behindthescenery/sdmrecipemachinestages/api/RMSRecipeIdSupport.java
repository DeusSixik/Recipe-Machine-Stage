package dev.behindthescenery.sdmrecipemachinestages.api;

import net.minecraft.resources.ResourceLocation;

public interface RMSRecipeIdSupport {

    void rms$setRecipeId(ResourceLocation recipeId);

    ResourceLocation rms$getRecipeId();
}
