package net.sdm.recipemachinestage.mixin.integration.farmers_delight;


import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.items.wrapper.RecipeWrapper;
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
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Optional;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public class CookingPotBlockEntityMixin {

    private CookingPotBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getMatchingRecipe", at = @At(value = "RETURN"), cancellable = true)
    private void sdm$getMatchingRecipe(RecipeWrapper inventoryWrapper, CallbackInfoReturnable<Optional<CookingPotRecipe>> cir){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ModRecipeTypes.COOKING.get())) return;

        Optional<CookingPotRecipe> recipe = cir.getReturnValue();
        if(recipe.isPresent()) {


            Optional<IOwnerBlock> d1 = thisBlockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisBlockEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                if (recipeBlockType != null) {
                    ServerPlayer player = PlayerHelper.getPlayerByGameProfile(thisBlockEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if (player != null) {
                        if (!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                            cir.setReturnValue(Optional.empty());
                        }
                    }
                }
            }
        }
    }
}
