//package net.sdm.recipemachinestage.mixin.integration.integrateddynamics;
//
//import net.sdm.recipemachinestage.SupportBlockData;
//import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
//import net.sdm.recipemachinestage.api.stage.StageContainer;
//import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
//import net.sdm.recipemachinestage.utils.PlayerHelper;
//import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
//import org.cyclops.integrateddynamics.blockentity.BlockEntityDryingBasin;
//import org.cyclops.integrateddynamics.core.recipe.type.RecipeDryingBasin;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.Optional;
//
//@Mixin(value = BlockEntityDryingBasin.class, remap = false)
//public class BlockEntityDryingBasinMixin {
//
//    @Unique
//    private BlockEntityDryingBasin recipe_machine_stage$thisEntity = RecipeStagesUtil.cast(this);
//
//    @Inject(method = "getCurrentRecipe", at = @At("RETURN"), cancellable = true)
//    public void sdm$getCurrentRecipe(CallbackInfoReturnable<Optional<RecipeDryingBasin>> cir) {
//        Optional<RecipeDryingBasin> recipeOptional = cir.getReturnValue();
//        if (recipeOptional.isPresent()) {
//            RecipeDryingBasin recipe = recipeOptional.get();
//
//            Optional<IOwnerBlock> d1 = recipe_machine_stage$thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
//            if (d1.isPresent() && recipe_machine_stage$thisEntity.getLevel().getServer() != null) {
//                IOwnerBlock ownerBlock = d1.get();
//                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
//                if(recipeBlockType != null) {
//                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisEntity.getLevel().getServer(), ownerBlock.getOwner());
//                    if(player != null) {
//                        if(!player.hasStage(recipeBlockType.stage)) {
//                            cir.setReturnValue(Optional.empty());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}
