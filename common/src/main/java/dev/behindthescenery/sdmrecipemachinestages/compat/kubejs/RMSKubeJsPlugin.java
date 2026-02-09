package dev.behindthescenery.sdmrecipemachinestages.compat.kubejs;

import dev.behindthescenery.sdmrecipemachinestages.RMSApi;
import dev.behindthescenery.sdmrecipemachinestages.supported.RMSSupportedTypes;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class RMSKubeJsPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        if(bindings.type().isServer()) {
            bindings.add("RMS", Methods.class);
        }
    }

    public interface Methods {

        static void addRecipe(String recipeType, String recipe_id, String stage) {
            RMSApi.addRecipe(recipeType, ResourceLocation.tryParse(recipe_id), stage);
        }

        static void addRecipes(String recipeType, String[] recipe_id, String stage) {
            RMSApi.addRecipe(recipeType, (ResourceLocation[]) Arrays.stream(recipe_id).map(ResourceLocation::tryParse).toArray(), stage);
        }

        static void addRecipeByMod(String recipeType, String modId, String stage) {
            RMSApi.addRecipeByMod(recipeType, modId, stage);
        }

        static void addRecipeByMods(String recipeType, String[] modId, String stage) {
            RMSApi.addRecipeByMod(recipeType, modId, stage);
        }

        static String[] getSupportedByTypes() {
            return RMSSupportedTypes.getSupportedByTypes();
        }

        static String[] getSupportedByMods() {
            return RMSSupportedTypes.getSupportedByMods();
        }

        static String[] getSupportedBlockClasses() {
            return RMSSupportedTypes.getSupportedBlockClasses();
        }

        static String[] getAllRecipeTypes() {
            return RMSApi.getAllRecipeTypes();
        }
    }
}
