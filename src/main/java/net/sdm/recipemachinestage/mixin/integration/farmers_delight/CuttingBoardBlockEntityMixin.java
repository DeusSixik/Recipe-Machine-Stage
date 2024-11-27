package net.sdm.recipemachinestage.mixin.integration.farmers_delight;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Optional;

@Mixin(value = CuttingBoardBlockEntity.class, remap = false)
public class CuttingBoardBlockEntityMixin {


    private CuttingBoardBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getMatchingRecipe", at = @At(value = "RETURN"), cancellable = true)
    private void sdm$getMatchingRecipe(RecipeWrapper recipeWrapper, ItemStack toolStack, Player player, CallbackInfoReturnable<Optional<CuttingBoardRecipe>> cir){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ModRecipeTypes.CUTTING.get())) return;

        Optional<CuttingBoardRecipe> recipe = cir.getReturnValue();
        if(recipe.isPresent()) {
            if (thisBlockEntity.getLevel().getServer() != null) {
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.get().getType(), recipe.get().getId());
                if (recipeBlockType != null) {
                    if (!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                        cir.setReturnValue(Optional.empty());
                    }
                }
            }
        }
    }
}
