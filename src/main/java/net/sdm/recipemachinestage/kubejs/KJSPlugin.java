package net.sdm.recipemachinestage.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import net.sdm.recipemachinestage.stage.StageContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class KJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        if(event.getType().isServer()) {
            event.add("RecipeMachineStage", RecipeMachineStageMethods.class);
        }
    }

    @Override
    public void registerEvents() {
    }

    public interface RecipeMachineStageMethods {

        static void addRecipe(String recipeType, String recipeID, String stage) {
            RecipeType<?> d1 = ForgeRegistries.RECIPE_TYPES.getValue(new ResourceLocation(recipeType));
            if(d1 == null) {
                ConsoleJS.getCurrent(ScriptManager.getCurrentContext()).error("Not found a recipe type \"" + recipeType + "\"");
                return;
            }
            StageContainer.registerRecipeJS(d1, new ResourceLocation(recipeID), stage);
            ConsoleJS.getCurrent(ScriptManager.getCurrentContext()).info("Recipe " + recipeID + " for " + d1 + " add to " + stage + ".stage");
        }

        static void addRecipes(String recipeType, List<String> recipeID, String stage) {
            RecipeType<?> d1 = ForgeRegistries.RECIPE_TYPES.getValue(new ResourceLocation(recipeType));
            if(d1 == null) {
                ConsoleJS.getCurrent(ScriptManager.getCurrentContext()).error("Not found a recipe type \"" + recipeType + "\"");
                return;
            }

            StageContainer.registerRecipeJS(d1, recipeID.stream().map(ResourceLocation::new).toList(), stage);
            ConsoleJS.getCurrent(ScriptManager.getCurrentContext()).info("Recipes " + recipeID + " for " + d1 + " add to " + stage + ".stage");
        }
    }
}
