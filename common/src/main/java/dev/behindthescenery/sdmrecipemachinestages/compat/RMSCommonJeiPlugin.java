package dev.behindthescenery.sdmrecipemachinestages.compat;

import dev.architectury.platform.Platform;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSStageUtilsClient;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@mezz.jei.api.JeiPlugin
public class RMSCommonJeiPlugin implements IModPlugin, IRecipeUpdateListener {

    public static IJeiRuntime runtime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.tryBuild(SdmRecipeMachineStages.MOD_ID, "main");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        RMSCommonJeiPlugin.runtime = jeiRuntime;
        RMSMain.addListener(this);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateRecipe() {
        final boolean isDebus = Platform.isDevelopmentEnvironment();
        final IRecipeManager recipeManager = runtime.getRecipeManager();

        for (final Map.Entry<RecipeType<? extends Recipe<?>>, List<RecipeBlockType>> entry :
                RMSContainer.Instance.getRecipesData().entrySet()) {

            final RecipeType recipeType = entry.getKey();
            final List<RecipeBlockType> recipeBlockTypes = entry.getValue();
            final ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);

            recipeManager.getRecipeType(recipeTypeId).ifPresent(jeiRecipeType -> {
                final Collection<RecipeHolder<?>> lockedRecipes = new ArrayList<>();
                final Collection<RecipeHolder<?>> unlockedRecipes = new ArrayList<>();

                assert Minecraft.getInstance().level != null;

                final List<RecipeHolder<?>> recipes =
                        (List<RecipeHolder<?>>) (List<?>) Minecraft.getInstance().level
                                .getRecipeManager()
                                .getAllRecipesFor(recipeType);

                for (final RecipeHolder<?> recipeHolder : recipes) {
                    final ResourceLocation recipeId = recipeHolder.id();

                    for (final RecipeBlockType recipeBlockType : recipeBlockTypes) {
                        if (!recipeBlockType.contains(recipeId)) continue;

                        if (RMSStageUtilsClient.isUnlocked(recipeBlockType)) {
                            unlockedRecipes.add(recipeHolder);
                        } else {
                            lockedRecipes.add(recipeHolder);
                        }
                    }
                }

                if (!lockedRecipes.isEmpty()) {
                    recipeManager.hideRecipes(jeiRecipeType, RMSUtils.cast(lockedRecipes));
                    if(isDebus) System.out.println("Hide:" + lockedRecipes.size());
                }
                if (!unlockedRecipes.isEmpty()) {
                    recipeManager.unhideRecipes(jeiRecipeType, RMSUtils.cast(unlockedRecipes));
                    if(isDebus) System.out.println("UnHide:" + lockedRecipes.size());
                }
            });
        }
    }
}
