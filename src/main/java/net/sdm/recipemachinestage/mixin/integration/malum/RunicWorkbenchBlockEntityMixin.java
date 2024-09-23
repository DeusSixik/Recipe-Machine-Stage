package net.sdm.recipemachinestage.mixin.integration.malum;

import com.sammy.malum.common.block.curiosities.runic_workbench.RunicWorkbenchBlockEntity;
import com.sammy.malum.common.recipe.RunicWorkbenchRecipe;
import com.sammy.malum.registry.common.recipe.RecipeTypeRegistry;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = RunicWorkbenchBlockEntity.class, remap = false)
public class RunicWorkbenchBlockEntityMixin {


    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lcom/sammy/malum/common/block/curiosities/runic_workbench/RunicWorkbenchBlockEntity;getItemPos()Lnet/minecraft/world/phys/Vec3;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$onUse(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack primaryInput, ItemStack secondaryInput, RunicWorkbenchRecipe recipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(RecipeTypeRegistry.RUNEWORKING.get())) return;

        RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
        if(recipeBlockType != null) {
            if(!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
