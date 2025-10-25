package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.mekanism;

import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSMekanismUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSRecipeUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mekanism.api.IContentsListener;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ICachedRecipeHolder;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;

@Mixin(RecipeCacheLookupMonitor.class)
public abstract class RecipeCacheLookupMonitorMixin<RECIPE extends MekanismRecipe<?>> implements ICachedRecipeHolder<RECIPE>, IContentsListener {

    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @Shadow
    protected CachedRecipe<RECIPE> cachedRecipe;
    @Shadow
    @Final
    protected int cacheIndex;
    @Shadow
    protected boolean shouldUnpause;
    @Unique
    private RMSMekanismUtils.CacheRecipe<RECIPE> sdm$cached_recipe;

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    public boolean updateAndProcess() {
        final CachedRecipe<RECIPE> oldCache = this.cachedRecipe;
        this.cachedRecipe = this.getUpdatedCache(this.cacheIndex);

        //RMS Fragment Start
        if(cachedRecipe != null && handler instanceof BlockEntity entity) {
            final RECIPE cacheRecipe = cachedRecipe.getRecipe();

            if(sdm$cached_recipe == null || sdm$cached_recipe.recipe() != cacheRecipe) {
                sdm$cached_recipe = RMSMekanismUtils.createCache(cacheRecipe);
            }

            final RMSRecipeUtils.RecipeValue<?> recipe = sdm$cached_recipe.recipeValue();
            if(recipe != null && !RMSUtils.canProcess(RMSUtils.getBlockOwner(entity), recipe.recipeHolder())) {
                return false;
            }
        }
        //RMS Fragment END

        if (this.cachedRecipe != oldCache) {
            this.handler.onCachedRecipeChanged(this.cachedRecipe, this.cacheIndex);
        }

        if (this.cachedRecipe != null) {
            if (this.shouldUnpause) {
                this.shouldUnpause = false;
                this.cachedRecipe.unpauseErrors();
            }

            this.cachedRecipe.process();
            return true;
        } else {
            return false;
        }
    }
}
