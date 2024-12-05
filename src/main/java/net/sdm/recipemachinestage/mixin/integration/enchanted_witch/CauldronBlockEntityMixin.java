package net.sdm.recipemachinestage.mixin.integration.enchanted_witch;

import favouriteless.enchanted.common.blocks.entity.CauldronBlockEntity;
import favouriteless.enchanted.common.recipes.CauldronTypeRecipe;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = CauldronBlockEntity.class, remap = false)
public class CauldronBlockEntityMixin<T extends CauldronTypeRecipe> {

    @Shadow private List<T> potentialRecipes;
    private CauldronBlockEntity thisEnity = RecipeStagesUtil.cast(this);


    @Inject(method = "setPotentialRecipes", at = @At("HEAD"), cancellable = true)
    private void sdm$setPotentialRecipes(List<T> potentialRecipes, CallbackInfo ci) {
        ci.cancel();
        List<T> newList = new ArrayList<>();

        Optional<IOwnerBlock> optionalOwnerBlock = thisEnity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && thisEnity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(thisEnity.getLevel().getServer(), ownerBlock.getOwner());
            if(playerData == null) return;

            for (T potentialRecipe : potentialRecipes) {
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(potentialRecipe.getType(), potentialRecipe.getId());

                if(recipeBlockType == null) continue;


                if (playerData.hasStage(recipeBlockType.stage)) {
                    newList.add(potentialRecipe);
                }
            }

        }

        if(newList.isEmpty()) {
            this.potentialRecipes = potentialRecipes;
        }

    }

}
