package net.sdm.recipemachinestage.mixin.integration.industrial_foregoing;

import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = FluidExtractorTile.class, remap = false)
public class FluidExtractorTileMixin {

    private FluidExtractorTile thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$findRecipe(Level world, BlockPos pos, CallbackInfoReturnable<FluidExtractorRecipe> cir){
        if(!StageContainer.hasRecipes(ModuleCore.FLUID_EXTRACTOR_TYPE.get())) return;

        FluidExtractorRecipe recipe = cir.getReturnValue();
        if(recipe == null) return;
        Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
