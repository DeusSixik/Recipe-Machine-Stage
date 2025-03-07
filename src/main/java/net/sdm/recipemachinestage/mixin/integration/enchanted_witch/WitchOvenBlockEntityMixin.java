package net.sdm.recipemachinestage.mixin.integration.enchanted_witch;


import favouriteless.enchanted.common.blocks.entity.WitchOvenBlockEntity;
import favouriteless.enchanted.common.recipes.ByproductRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = WitchOvenBlockEntity.class, remap = false)
public class WitchOvenBlockEntityMixin {

    private WitchOvenBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "canBurn", at = @At("HEAD"), cancellable = true)
    private void sdm$canBurn(@Nullable Recipe<?> recipe, CallbackInfoReturnable<Boolean> cir) {

        if(recipe == null) return;

        Optional<IOwnerBlock> optionalOwnerBlock = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());

            if(playerData == null) return;
            if(recipeBlockType == null) return;

            if (!playerData.hasStage(recipeBlockType.stage)) {
                cir.setReturnValue(false);
            }

        }
    }

    @Inject(method = "tryCreateByproduct", at = @At("HEAD"), cancellable = true)
    private void sdm$tryCreateByproduct(ByproductRecipe recipe, CallbackInfo ci) {

        if(recipe == null) return;

        Optional<IOwnerBlock> optionalOwnerBlock = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());

            if(playerData == null) return;
            if(recipeBlockType == null) return;

            if (!playerData.hasStage(recipeBlockType.stage)) {
                ci.cancel();
            }

        }
    }
}
