package dev.behindthescenery.sdmrecipemachinestages.compat;

import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.compat.jei.JeiRecipeType;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import dev.behindthescenery.sdmrecipemachinestages.mixin.jei.RecipeManagerAccessor;
import dev.behindthescenery.sdmrecipemachinestages.mixin.jei.RecipeManagerInternalAccessor;
import dev.behindthescenery.sdmrecipemachinestages.mixin.jei.RecipeTypeDataMapAccessor;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSStageUtilsClient;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSTextConverter;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.recipes.RecipeManagerInternal;
import mezz.jei.library.recipes.collect.RecipeTypeData;
import mezz.jei.library.recipes.collect.RecipeTypeDataMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@mezz.jei.api.JeiPlugin
public class RMSCommonJeiPlugin implements IModPlugin, IRecipeUpdateListener {

    private static final Logger LOGGER = LogUtils.getLogger();

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
        if(Minecraft.getInstance().level == null) return;

        final boolean isDebus = Platform.isDevelopmentEnvironment();
        final IRecipeManager recipeManager = runtime.getRecipeManager();


        if(Platform.isDevelopmentEnvironment()) {
            System.out.println("-------------------------");
            RecipeManagerInternal internal = ((RecipeManagerAccessor) recipeManager).getInternal();
            RecipeTypeDataMap recipeTypeDataMap = ((RecipeManagerInternalAccessor) internal).getRecipeTypeDataMap();
            Map<mezz.jei.api.recipe.RecipeType<?>, RecipeTypeData<?>> recipeTypeMap = ((RecipeTypeDataMapAccessor) recipeTypeDataMap).getUidMap();

            for (Map.Entry<mezz.jei.api.recipe.RecipeType<?>, RecipeTypeData<?>> entry : recipeTypeMap.entrySet()) {
                System.out.println(entry.getKey().getUid());
            }
            System.out.println("-------------------------");
        }

        for (final Map.Entry<RecipeType<? extends Recipe<?>>, List<RecipeBlockType>> entry :
                RMSContainer.Instance.getRecipesByTypeData().entrySet()) {

            final RecipeType recipeType = entry.getKey();
            final List<RecipeBlockType> recipeBlockTypes = entry.getValue();
            final ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);

            final ResourceLocation[] converted = RMSTextConverter.tryConvert(recipeTypeId);

            for (int i = 0; i < converted.length; i++) {
                recipeManager.getRecipeType(converted[i]).ifPresent(jeiRecipeType -> {
                    final JeiRecipeType recipeType_enum = JeiRecipeType.getRecipeTypeEnum(jeiRecipeType.getRecipeClass());

                    final Collection<Element<?>> lockedRecipes = new ArrayList<>();
                    final Collection<Element<?>> unlockedRecipes = new ArrayList<>();

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
                                switch (recipeType_enum) {
                                    case Holder -> unlockedRecipes.add(new RecipeHolderElement(recipeHolder));
                                    case Recipe -> unlockedRecipes.add(new RecipeElement(recipeHolder.value()));
                                }
                            } else {
                                switch (recipeType_enum) {
                                    case Holder -> lockedRecipes.add(new RecipeHolderElement(recipeHolder));
                                    case Recipe -> lockedRecipes.add(new RecipeElement(recipeHolder.value()));
                                }
                            }
                        }
                    }

                    if (!lockedRecipes.isEmpty()) {
                        recipeManager.hideRecipes(jeiRecipeType, RMSUtils.cast(lockedRecipes.stream().map(Element::value).toList()));
                        if(isDebus)
                            LOGGER.info("{} Hide:{}", recipeTypeId, lockedRecipes.size());
                    }
                    if (!unlockedRecipes.isEmpty()) {
                        recipeManager.unhideRecipes(jeiRecipeType, RMSUtils.cast(unlockedRecipes.stream().map(Element::value).toList()));
                        if(isDebus)
                            LOGGER.info("{} UnHide:{}", recipeTypeId, lockedRecipes.size());
                    }
                });
            }
        }

        RMSContainer.send("Invoke 1.1");
    }

    protected record RecipeHolderElement(RecipeHolder<?> value) implements Element<RecipeHolder<?>> { }

    protected record RecipeElement(Recipe<?> value) implements Element<Recipe<?>> { }

    protected interface Element<T> {

        T value();
    }
}
