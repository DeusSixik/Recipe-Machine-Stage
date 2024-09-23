package net.sdm.recipemachinestage.mixin.integration.botania;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.common.block.block_entity.RunicAltarBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.Optional;

@Mixin(value = RunicAltarBlockEntity.class, remap = false)
public class RunicAltarBlockEntityMixin {

    @Unique
    public RunicAltarBlockEntity recipe_machine_stage$thisBlockEntity = (RunicAltarBlockEntity)(Object)this;


    @Inject(method = "onUsedByWand", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$onUsedByWand(Player player, ItemStack wand, Direction side, CallbackInfoReturnable<Boolean> cir, RunicAltarRecipe recipe, Optional maybeRecipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.RUNE_TYPE)) return;

        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisBlockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && player.getServer() != null) {
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                if(!GameStageHelper.hasStage(player, recipeBlockType.stage)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
