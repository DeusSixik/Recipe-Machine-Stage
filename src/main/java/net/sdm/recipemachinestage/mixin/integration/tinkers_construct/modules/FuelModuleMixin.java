package net.sdm.recipemachinestage.mixin.integration.tinkers_construct.modules;

import net.minecraft.world.level.material.Fluid;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
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
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

import java.util.Optional;

@Mixin(value = FuelModule.class, remap = false)
public class FuelModuleMixin {

    @Shadow @Final private MantleBlockEntity parent;

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$findRecipe(Fluid fluid, CallbackInfoReturnable<MeltingFuel> cir) {
        MeltingFuel recipe = cir.getReturnValue();
        if(recipe == null) return;

        Optional<IOwnerBlock> d1 = parent.getCapability(RMSCapability.BLOCK_OWNER).resolve();
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
