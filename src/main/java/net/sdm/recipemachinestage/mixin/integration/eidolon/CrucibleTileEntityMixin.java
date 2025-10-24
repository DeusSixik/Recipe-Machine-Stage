package net.sdm.recipemachinestage.mixin.integration.eidolon;

import elucent.eidolon.common.tile.CrucibleTileEntity;
import elucent.eidolon.recipe.CrucibleRecipe;
import elucent.eidolon.recipe.CrucibleRegistry;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@Mixin(value = CrucibleTileEntity.class, remap = false)
public class CrucibleTileEntityMixin {

    private CrucibleTileEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "handleTurnBasedUpdate", at = @At(value = "INVOKE", target = "Lelucent/eidolon/recipe/CrucibleRegistry;find(Ljava/util/List;)Lelucent/eidolon/recipe/CrucibleRecipe;"))
    public CrucibleRecipe sdm$handleTurnBasedUpdate(List<CrucibleTileEntity.CrucibleStep> recipeMap){
        return sdm$inject$getRecipe(recipeMap);
    }

    @Redirect(method = "handleTimedUpdate", at = @At(value = "INVOKE", target = "Lelucent/eidolon/recipe/CrucibleRegistry;find(Ljava/util/List;)Lelucent/eidolon/recipe/CrucibleRecipe;"))
    public CrucibleRecipe sdm$handleTimedUpdate(List<CrucibleTileEntity.CrucibleStep> recipeMap){
        return sdm$inject$getRecipe(recipeMap);
    }

    @Unique
    private CrucibleRecipe sdm$inject$getRecipe(List<CrucibleTileEntity.CrucibleStep> recipeMap) {
        CrucibleRecipe recipe = CrucibleRegistry.find(recipeMap);
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return recipe;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        return null;
                    }
                }
            }
        }

        return recipe;
    }
}
