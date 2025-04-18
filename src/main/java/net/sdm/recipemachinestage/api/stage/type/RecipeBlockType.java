package net.sdm.recipemachinestage.api.stage.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeBlockType {

    public String stage;
    public List<ResourceLocation> recipesID = new ArrayList<>();
    public List<Recipe<?>> recipes = new ArrayList<>();

    public RecipeBlockType(String stage){
        this.stage = stage;
    }


    public boolean addRecipeType(RecipeBlockType recipe){
        if(Objects.equals(recipe.stage, stage)) {
            this.recipesID.addAll(recipe.recipesID);
            return true;
        }
        return false;
    }

    public boolean contains(ResourceLocation recipesID) {
        if(this.recipesID.contains(recipesID)) return true;

        var optional = this.recipesID.stream().filter(s -> Objects.equals(s, recipesID)).findFirst();
        return optional.isPresent();
    }
}
