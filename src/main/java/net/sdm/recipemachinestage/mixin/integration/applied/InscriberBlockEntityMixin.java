package net.sdm.recipemachinestage.mixin.integration.applied;

import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InscriberBlockEntity.class, remap = false)
public class InscriberBlockEntityMixin {

    private InscriberBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getTask", at = @At("RETURN"), cancellable = true)
    public void sdm$getTask(CallbackInfoReturnable<InscriberRecipe> cir){

        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), thisEntity));

//        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(InscriberRecipe.TYPE)) return;
//
//        InscriberRecipe recipe = cir.getReturnValue();
//        if(recipe != null) {
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
