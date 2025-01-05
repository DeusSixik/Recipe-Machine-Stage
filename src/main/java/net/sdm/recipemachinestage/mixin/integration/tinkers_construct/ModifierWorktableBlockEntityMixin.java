package net.sdm.recipemachinestage.mixin.integration.tinkers_construct;

import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.tables.block.entity.table.ModifierWorktableBlockEntity;

import java.util.Optional;

@Mixin(value = ModifierWorktableBlockEntity.class, remap = false)
public class ModifierWorktableBlockEntityMixin {

    @Unique
    private ModifierWorktableBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getCurrentRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getCurrentRecipe(CallbackInfoReturnable<IModifierWorktableRecipe> cir) {
        IModifierWorktableRecipe recipe = cir.getReturnValue();
        if(recipe == null) return;

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }

    }
}
