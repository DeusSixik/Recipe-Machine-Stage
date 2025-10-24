package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ImbuementTile.class,remap = false)
public class ImbuementTileMixin {

//    public ImbuementTile thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipeNow", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipeNow(CallbackInfoReturnable<IImbuementRecipe> cir){
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), RecipeStagesUtil.cast(this)));
//        IImbuementRecipe recipe = cir.getReturnValue();
//        if(recipe != null) {
//            if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return;
//
//            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
//            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
//                IOwnerBlock ownerBlock = d1.get();
//                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
//                if(recipeBlockType != null) {
//                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
//                    if(player != null) {
//                        if(!player.hasStage(recipeBlockType.stage)) {
//                            cir.setReturnValue(null);
//                        }
//                    }
//                }
//            }
//        }
    }
}
