package net.sdm.recipemachinestage.compat.jei;

import com.blamejared.crafttweaker.api.recipe.manager.RecipeManagerWrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.recipes.RecipeManagerInternal;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import net.sdm.recipemachinestage.mixin.jei.RecipeManagerAccessor;
import net.sdm.recipemachinestage.mixin.jei.RecipeManagerInternalAccessor;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.ReflectionHelper;

import java.util.List;
import java.util.Map;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static IJeiRuntime runTime;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("recipemachinestage:main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runTime = jeiRuntime;
    }

    public static<C extends Container, T extends Recipe<C>> void sync(IStageData data) {

        if(Minecraft.getInstance().level == null) {
            return;
        }


        for (Map.Entry<RecipeType<?>, List<RecipeBlockType>> entry : StageContainer.INSTANCE.RECIPES_STAGES.entrySet()) {
            RecipeType<T> recipeType = (RecipeType<T>) entry.getKey();
            List<RecipeBlockType> recipeBlockTypes = entry.getValue();

            RecipeManagerWrapper recipes = RecipeManagerWrapper.makeOrNull((RecipeType<Recipe<?>>) recipeType);


            for (Recipe<?> recipe : recipes) {

                for (RecipeBlockType recipeBlockType : recipeBlockTypes) {
                    if (recipeBlockType.recipesID.contains(recipe.getId())) {


                        for (IRecipeCategory<?> category : getCategories()) {
                            if(ReflectionHelper.canCast(category.getRecipeType().getRecipeClass(), recipe.getClass())) {
                                if(data.hasStage(recipeBlockType.stage)) {
                                    JEIPlugin.runTime.getRecipeManager().unhideRecipes(category.getRecipeType(), RecipeStagesUtil.cast(List.of(recipe)));
                                } else {
                                    JEIPlugin.runTime.getRecipeManager().hideRecipes(category.getRecipeType(), RecipeStagesUtil.cast(List.of(recipe)));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static RecipeManagerInternal getInternal() {
       return ((RecipeManagerAccessor)runTime.getRecipeManager()).getInternal();
    }

    public static List<IRecipeCategory<?>> getCategories() {
       return ((RecipeManagerInternalAccessor)getInternal()).getRecipeCategories();
    }
}
