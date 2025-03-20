package net.sdm.recipemachinestage.mixin.integration.thermal.thermal_extras;

import cofh.thermal.lib.util.recipes.IThermalInventory;
import cofh.thermal.lib.util.recipes.internal.IMachineRecipe;
import mrthomas20121.thermal_extra.block.entity.MachineEndothermicDehydratorBlockEntity;
import mrthomas20121.thermal_extra.recipe.EndothermicDehydratorRecipeManager;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.compat.thermal.IThermalRecipeAddition;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = MachineEndothermicDehydratorBlockEntity.class, remap = false)
public class MachineEndothermicDehydratorBlockEntityMixin {

    private MachineEndothermicDehydratorBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "cacheRecipe", at = @At(value = "INVOKE", target = "Lmrthomas20121/thermal_extra/recipe/EndothermicDehydratorRecipeManager;getRecipe(Lcofh/thermal/lib/util/recipes/IThermalInventory;)Lcofh/thermal/lib/util/recipes/internal/IMachineRecipe;"))
    private IMachineRecipe sdm$cacheRecipe$getRecipe(EndothermicDehydratorRecipeManager instance, IThermalInventory inventory) {
        IMachineRecipe recipe = instance.getRecipe(thisEntity);

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null && recipe instanceof IThermalRecipeAddition recipeAddition) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipeAddition.getRecipeType(), recipeAddition.getRecipeID());
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
