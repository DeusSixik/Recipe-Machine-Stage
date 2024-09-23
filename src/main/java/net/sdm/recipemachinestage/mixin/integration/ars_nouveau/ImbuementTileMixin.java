package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = ImbuementTile.class,remap = false)
public class ImbuementTileMixin {

    public ImbuementTile thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipeNow", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipeNow(CallbackInfoReturnable<IImbuementRecipe> cir){
        IImbuementRecipe recipe = cir.getReturnValue();
        if(recipe != null) {
            if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return;

            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    ServerPlayer player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                            cir.setReturnValue(null);
                        }
                    }
                }
            }
        }
    }
}
