package net.sdm.recipemachinestage.mixin.integration.tinkers_construct.modules;

import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModule;

import java.util.Optional;

@Mixin(value = MeltingModule.class, remap = false)
public class MeltingModuleMixin {


    @Shadow @Final private MantleBlockEntity parent;

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$findRecipe(CallbackInfoReturnable<IMeltingRecipe> cir) {
        IMeltingRecipe recipe = cir.getReturnValue();
        if(recipe == null) return;

        Optional<IOwnerBlock> d1 = parent.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && parent.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(parent.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
