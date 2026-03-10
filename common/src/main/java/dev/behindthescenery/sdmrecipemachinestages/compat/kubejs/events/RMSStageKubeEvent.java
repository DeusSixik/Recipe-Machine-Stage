package dev.behindthescenery.sdmrecipemachinestages.compat.kubejs.events;

import dev.behindthescenery.sdmrecipemachinestages.api.RMSApi;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class RMSStageKubeEvent implements KubeEvent {

    public RMSStageKubeEvent() {}

    public void addRecipe(String recipeType, String recipe_id, String stage) {
        RMSApi.addRecipe(recipeType, ResourceLocation.tryParse(recipe_id), stage);
    }

    public void addRecipes(String recipeType, String[] recipe_id, String stage) {
        RMSApi.addRecipe(recipeType, (ResourceLocation[]) Arrays.stream(recipe_id).map(ResourceLocation::tryParse).toArray(), stage);
    }

    public void addRecipeByMod(String recipeType, String modId, String stage) {
        RMSApi.addRecipeByMod(recipeType, modId, stage);
    }

    public void addRecipeByMods(String recipeType, String[] modId, String stage) {
        RMSApi.addRecipeByMod(recipeType, modId, stage);
    }

    public void addRecipeByMachine(String recipeType, String stage) {
        RMSApi.addRecipeByMachine(recipeType, stage);
    }
}
