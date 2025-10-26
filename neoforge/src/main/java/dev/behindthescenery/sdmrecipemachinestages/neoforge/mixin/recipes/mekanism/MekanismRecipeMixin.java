package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.mekanism;

import dev.behindthescenery.sdmrecipemachinestages.api.RMSRecipeIdSupport;
import mekanism.api.recipes.MekanismRecipe;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MekanismRecipe.class)
public class MekanismRecipeMixin implements RMSRecipeIdSupport {

    @Unique
    private ResourceLocation rms$recipe_id;


    @Override
    public void rms$setRecipeId(ResourceLocation recipeId) {
        this.rms$recipe_id = recipeId;
    }

    @Override
    public ResourceLocation rms$getRecipeId() {
        return rms$recipe_id;
    }
}
