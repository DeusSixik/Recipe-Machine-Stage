package net.sdm.recipemachinestage.mixin.integration.botania;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
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

        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisBlockEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && recipe_machine_stage$thisBlockEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData _player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisBlockEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(_player != null) {
                    if(!_player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
