package net.sdm.recipemachinestage.mixin.integration.botania;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.Optional;

@Mixin(value = ManaPoolBlockEntity.class, remap = false)
public class ManaPoolBlockEntityMixin {
//
    @Unique
    public ManaPoolBlockEntity recipe_machine_stage$thisBlockEntity = (ManaPoolBlockEntity)(Object)this;

    @Inject(method = "collideEntityItem", at = @At(value = "INVOKE", target = "Lvazkii/botania/api/recipe/ManaInfusionRecipe;getManaToConsume()I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void sdm$collideEntityItem(ItemEntity item, CallbackInfoReturnable<Boolean> cir, ItemStack stack, ManaInfusionRecipe recipe, Item var4){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.MANA_INFUSION_TYPE)) return;

        Optional<IOwnerBlock> d1 = recipe_machine_stage$thisBlockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
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
