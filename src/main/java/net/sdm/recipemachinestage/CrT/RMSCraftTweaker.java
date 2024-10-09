package net.sdm.recipemachinestage.CrT;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.bracket.BracketHandlers;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.sdm.recipemachinestage.CrT.action.RMSAction;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@Document("mods/recipemachinestage/RecipeMachineStage")
@ZenCodeType.Name("mods.recipemachinestage.RecipeMachineStage")
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
        CraftTweakerAPI.apply(new RMSAction(BracketHandlers.getRecipeManager(recipeType).getRecipeType(), new ArrayList<>(List.of(recipeID)), stage));
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
        CraftTweakerAPI.apply(new RMSAction(BracketHandlers.getRecipeManager(recipeType).getRecipeType(), new ArrayList<>(List.of(recipeID)), stage));
    }

    @ZenCodeType.Method
    public static void addRecipe(IRecipeManager<Recipe<Container>> recipeType, String recipeID, String stage) {
        CraftTweakerAPI.apply(new RMSAction(recipeType.getRecipeType(), new ArrayList<>(List.of(recipeID)), stage));
    }

    @ZenCodeType.Method
    public static void addRecipe(IRecipeManager<Recipe<Container>> recipeType, String[] recipeID, String stage) {
        CraftTweakerAPI.apply(new RMSAction(recipeType.getRecipeType(), new ArrayList<>(List.of(recipeID)), stage));
    }
}
