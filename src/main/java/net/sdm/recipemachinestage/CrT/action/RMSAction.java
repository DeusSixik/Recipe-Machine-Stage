package net.sdm.recipemachinestage.CrT.action;

import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.stage.StageContainer;

import java.util.List;

public class RMSAction implements IRuntimeAction {

    public final RecipeType<?> recipeType;
    public final List<String> recipeName;
    public final String stage;

    public RMSAction(RecipeType<?> recipeType, List<String> recipeName, String stage) {
        this.recipeType = recipeType;
        this.recipeName = recipeName;
        this.stage = stage;
    }

    @Override
    public void apply() {
        StageContainer.registerRecipe(recipeType, recipeName.stream().map(ResourceLocation::new).toList(), stage);
    }

    @Override
    public String describe() {
        return recipeType + " add recipe " + recipeName + " to " + stage + ".stage";
    }

    @Override
    public String systemName() {
        return "Recipe Machine Stage";
    }
}
