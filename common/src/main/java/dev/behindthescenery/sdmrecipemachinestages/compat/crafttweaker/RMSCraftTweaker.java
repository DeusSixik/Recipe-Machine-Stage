package dev.behindthescenery.sdmrecipemachinestages.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.apache.logging.log4j.Level;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ZenRegister
@ZenCodeType.Name("mods.rms.RMS")
public class RMSCraftTweaker {

    /**
     * The method that blocks the recipe
     * @param recipeType Recipe Type (In CraftTweaker <recipeType:minecraft:smelting> you need write without prefix <recipeType>. "minecraft:smelting")
     * @param recipeID Recipe ID ("minecraft:iron_ingot_from_blasting_iron_ore", "mekanism:processing/iron/enriched" etc.);
     * @param stage Stage who block the recipe ("one" etc.)
     * @docParam recipeType "minecraft:smelting"
     * @docParam recipeID "minecraft:stone"
     * @docParam stage "one"
     */
    @ZenCodeType.Method
    public static void addRecipe(String recipeType, String recipeID, String stage) {
        RMSApi.register(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), new ArrayList<>(List.of(ResourceLocation.tryParse(recipeID))), stage);
    }

    /**
     * A method that blocks multiple recipes
     * @param recipeType Recipe Type (In CraftTweaker <recipeType:minecraft:smelting> you need write without prefix <recipeType>. "minecraft:smelting")
     * @param recipeID Recipe IDs ("minecraft:iron_ingot_from_blasting_iron_ore", "mekanism:processing/iron/enriched" etc.);
     * @param stage Stage who block the recipe ("one" etc.)
     * @docParam recipeType "minecraft:smelting"
     * @docParam recipeID ["minecraft:stone", "minecraft:iron_ingot"]
     * @docParam stage "one"
     */
    @ZenCodeType.Method
    public static void addRecipe(String recipeType, String[] recipeID, String stage) {
        RMSApi.register(BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.tryParse(recipeType)), new ArrayList<>(Stream.of(recipeID).map(ResourceLocation::tryParse).toList()), stage);
    }

    @ZenCodeType.Method
    public static void addRecipe(IRecipeManager<Recipe<RecipeInput>> recipeType, String recipeID, String stage) {
        RMSApi.register(recipeType.getRecipeType(), new ArrayList<>(Stream.of(recipeID).map(ResourceLocation::tryParse).toList()), stage);
    }

    @ZenCodeType.Method
    public static void addRecipe(IRecipeManager<Recipe<RecipeInput>> recipeType, String[] recipeID, String stage) {
        RMSApi.register(recipeType.getRecipeType(), new ArrayList<>(Stream.of(recipeID).map(ResourceLocation::tryParse).toList()), stage);
    }

    @ZenCodeType.Method
    public static void addRecipeByMod(IRecipeManager<Recipe<RecipeInput>> recipeType, String modId, String stage) {
        RMSApi.addRecipeByMod(recipeType.getRecipeType(), modId, stage);
    }

    @ZenCodeType.Method
    public static void addRecipeByMod(IRecipeManager<Recipe<RecipeInput>> recipeType, String[] modIds, String stage) {
        RMSApi.addRecipeByMod(recipeType.getRecipeType(), modIds, stage);
    }

    @ZenCodeType.Method
    public static void printAllRecipes(IRecipeManager<Recipe<RecipeInput>> recipeType) {
        RMSApi.printAllRecipes(recipeType.getRecipeType(), s -> CraftTweakerAPI.getLogger("RMS").printf(Level.INFO, s));
    }
}
