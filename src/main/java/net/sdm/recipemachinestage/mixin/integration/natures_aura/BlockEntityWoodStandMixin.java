package net.sdm.recipemachinestage.mixin.integration.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = BlockEntityWoodStand.class, remap = false)
public class BlockEntityWoodStandMixin {
    private BlockEntityWoodStand thisEntity = RecipeStagesUtil.cast(this);

    @Shadow private TreeRitualRecipe recipe;

    @Inject(method = "isRitualOkay", at = @At("HEAD"), cancellable = true)
    public void sdm$isRitualOkay(CallbackInfoReturnable<Boolean> cir){

        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ModRecipes.TREE_RITUAL_TYPE)) return;

        if(recipe == null) return;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
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
