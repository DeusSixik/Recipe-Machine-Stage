package net.sdm.recipemachinestage.mixin.integration.thermal.recipe;

import cofh.thermal.lib.util.recipes.internal.BaseMachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.compat.thermal.IThermalRecipeAddition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BaseMachineRecipe.class, remap = false)
public class BaseMachineRecipeMixin implements IThermalRecipeAddition {

    @Unique
    private ResourceLocation recipe_machine_stage$recipeID;
    @Unique
    private RecipeType<?> recipe_machine_stage$recipeType;

    @Override
    public ResourceLocation getRecipeID() {
        return recipe_machine_stage$recipeID;
    }

    @Override
    public void setRecipeID(ResourceLocation recipeID) {
        this.recipe_machine_stage$recipeID = recipeID;
    }

    @Override
    public RecipeType<?> getRecipeType() {
        return recipe_machine_stage$recipeType;
    }

    @Override
    public void setRecipeType(RecipeType<?> recipeType) {
        this.recipe_machine_stage$recipeType = recipeType;
    }
}
