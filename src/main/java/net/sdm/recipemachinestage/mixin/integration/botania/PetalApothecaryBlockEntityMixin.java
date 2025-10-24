package net.sdm.recipemachinestage.mixin.integration.botania;


import net.minecraft.world.entity.item.ItemEntity;
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
import vazkii.botania.api.recipe.PetalApothecaryRecipe;
import vazkii.botania.common.block.block_entity.PetalApothecaryBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.Optional;

@Mixin(value = PetalApothecaryBlockEntity.class, remap = false)
public class PetalApothecaryBlockEntityMixin {

    @Unique
    public PetalApothecaryBlockEntity recipe_machine_stage$thisBlockEntity = (PetalApothecaryBlockEntity)(Object)this;


    @Inject(method = "collideEntityItem", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/block/block_entity/PetalApothecaryBlockEntity;saveLastRecipe(Lnet/minecraft/world/item/crafting/Ingredient;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$collideEntityItem(ItemEntity item, CallbackInfoReturnable<Boolean> cir, ItemStack stack, Optional maybeRecipe, PetalApothecaryRecipe recipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.PETAL_TYPE)) return;

        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisBlockEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent() && item.getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(item.getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
