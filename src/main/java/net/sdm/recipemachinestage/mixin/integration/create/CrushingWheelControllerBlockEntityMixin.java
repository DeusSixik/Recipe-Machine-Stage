package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraftforge.items.wrapper.RecipeWrapper;
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

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public class CrushingWheelControllerBlockEntityMixin {

    private CrushingWheelControllerBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$findRecipe(CallbackInfoReturnable<Optional<ProcessingRecipe<RecipeWrapper>>> cir) {
        if (StageContainer.hasRecipes(AllRecipeTypes.CRUSHING.getType()) || StageContainer.hasRecipes(AllRecipeTypes.MILLING.getType())) {


            Optional<ProcessingRecipe<RecipeWrapper>> recipeOptional = cir.getReturnValue();
            if (recipeOptional.isPresent()) {
                Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
                if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                    IOwnerBlock ownerBlock = d1.get();
                    RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipeOptional.get().getType(), recipeOptional.get().getId());
                    if (recipeBlockType != null) {
                        PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                        if (player != null) {
                            if (!player.hasStage(recipeBlockType.stage)) {
                                cir.setReturnValue(Optional.empty());
                            }
                        }
                    }
                }
            }
        }
    }
}
