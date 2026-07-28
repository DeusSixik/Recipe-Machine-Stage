package net.sdm.recipemachinestage.mixin.impl;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sdm.recipemachinestage.impl.patches.RecipeIndex;
import net.sdm.recipemachinestage.impl.patches.RecipeManagerExtern;
import net.sdm.recipemachinestage.utils.RMSUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements RecipeManagerExtern {

    @Unique
    private static final Comparator<Map.Entry<ResourceLocation, Recipe<?>>> GPR$RECIPE_ORDER =
            Comparator.comparing(Map.Entry::getKey);

    @Shadow
    private Map<ResourceLocation, Recipe<?>> byName;
    @Unique
    private volatile Recipe[] recipesCache;

    @Unique
    private volatile Map<ResourceLocation, Integer> recipeIndexCache;

    @Unique
    private volatile ResourceLocation[] recipeIdsCache;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void gpr$init(HolderLookup.Provider registries, CallbackInfo ci) {
        RMSUtils.setRecipeManager(RMSUtils.cast(this));
    }

    @Override
    public Recipe[] getRecipesRaw() {
        if (recipesCache == null) {
            gpr$initCache();
        }

        return recipesCache;
    }

    @Override
    public int getRecipeIndex(ResourceLocation id) {
        if (recipeIndexCache == null) {
            gpr$initCache();
        }

        Integer index = recipeIndexCache.get(id);
        return index == null ? -1 : index;
    }

    @Override
    public Recipe<?> getRecipeByIndex(int index) {
        var recipes = getRecipesRaw();
        if(recipes.length <= index)
            return null;

        return recipes[index];
    }

    @Override
    public ResourceLocation getRecipeIdByIndex(int index) {
        if (recipeIdsCache == null) {
            gpr$initCache();
        }

        ResourceLocation[] ids = recipeIdsCache;
        return index >= 0 && index < ids.length ? ids[index] : null;
    }

    @Unique
    private void gpr$initCache() {
        if (recipesCache != null && recipeIndexCache != null && recipeIdsCache != null) {
            return;
        }

        synchronized (this) {
            if (recipesCache != null && recipeIndexCache != null && recipeIdsCache != null) {
                return;
            }

            List<Map.Entry<ResourceLocation, Recipe<?>>> sortedEntries = new ArrayList<>(byName.entrySet());
            sortedEntries.sort(GPR$RECIPE_ORDER);

            RecipeHolder[] holdersCache = new RecipeHolder[sortedEntries.size()];
            Recipe[] recipesCache = new Recipe[sortedEntries.size()];
            Map<ResourceLocation, Integer> recipeIndexCache = new HashMap<>(sortedEntries.size());
            ResourceLocation[] recipeIdsCache = new ResourceLocation[sortedEntries.size()];

            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<ResourceLocation, Recipe<?>> entry = sortedEntries.get(i);
                Recipe<?> recipe = entry.getValue();

                recipesCache[i] = recipe;
                recipeIndexCache.put(entry.getKey(), i);
                recipeIdsCache[i] = entry.getKey();

                RecipeIndex.get(recipe).gpr$setIndex(i);
            }

            this.recipesCache = recipesCache;
            this.recipeIndexCache = recipeIndexCache;
            this.recipeIdsCache = recipeIdsCache;
        }
    }
}
