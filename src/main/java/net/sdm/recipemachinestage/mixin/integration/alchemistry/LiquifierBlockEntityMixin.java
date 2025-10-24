package net.sdm.recipemachinestage.mixin.integration.alchemistry;

import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = LiquifierBlockEntity.class, remap = false)
public class LiquifierBlockEntityMixin {

    private LiquifierBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "setRecipe", at = @At("HEAD"), cancellable = true)
    public <R extends AbstractProcessingRecipe> void sdm$setRecipe(@Nullable R recipe, CallbackInfo ci){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RecipeRegistry.LIQUIFIER_TYPE.get())) return;

        if(!(recipe instanceof LiquifierRecipe)) return;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
