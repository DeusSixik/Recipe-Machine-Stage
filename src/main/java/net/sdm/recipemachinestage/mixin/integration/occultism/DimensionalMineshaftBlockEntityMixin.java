package net.sdm.recipemachinestage.mixin.integration.occultism;

import com.klikli_dev.occultism.common.blockentity.DimensionalMineshaftBlockEntity;
import com.klikli_dev.occultism.crafting.recipe.MinerRecipe;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = DimensionalMineshaftBlockEntity.class,remap = false)
public class DimensionalMineshaftBlockEntityMixin {

    private DimensionalMineshaftBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "mine", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    public int sdm$mine(List instance){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(OccultismRecipes.MINER_TYPE.get())) return instance.size();

        Iterator iterator = instance.iterator();
        while (iterator.hasNext()){
            MinerRecipe recipe = (MinerRecipe) iterator.next();
            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

        return instance.size();
    }
}
