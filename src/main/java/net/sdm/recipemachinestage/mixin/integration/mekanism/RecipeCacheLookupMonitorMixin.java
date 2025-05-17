package net.sdm.recipemachinestage.mixin.integration.mekanism;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = RecipeCacheLookupMonitor.class, remap = false)
public class RecipeCacheLookupMonitorMixin<RECIPE extends MekanismRecipe> {

    public RecipeCacheLookupMonitor<RECIPE> thisCache = RecipeStagesUtil.cast(this);

    @Shadow protected CachedRecipe<RECIPE> cachedRecipe;

    @Shadow @Final protected int cacheIndex;

    @Shadow @Final private IRecipeLookupHandler<RECIPE> handler;

    @Inject(method = "updateAndProcess()Z", at = @At("HEAD"), cancellable = true)
    public void sdm$updateAndProcess(CallbackInfoReturnable<Boolean> cir){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty()) return;
        this.cachedRecipe = thisCache.getUpdatedCache(this.cacheIndex);

        if(cachedRecipe != null && handler instanceof BlockEntity thisEntity) {
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(cachedRecipe.getRecipe().getType(), cachedRecipe.getRecipe().getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.setReturnValue(false);
                        }
                    }
                }
            }
        }

    }
}
