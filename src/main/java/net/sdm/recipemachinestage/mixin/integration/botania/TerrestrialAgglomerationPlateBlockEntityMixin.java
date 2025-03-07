package net.sdm.recipemachinestage.mixin.integration.botania;


import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe;
import vazkii.botania.common.block.block_entity.TerrestrialAgglomerationPlateBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;

import java.util.List;
import java.util.Optional;

@Mixin(value = TerrestrialAgglomerationPlateBlockEntity.class, remap = false)
public class TerrestrialAgglomerationPlateBlockEntityMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/block/block_entity/TerrestrialAgglomerationPlateBlockEntity;getAttachedSpark()Lvazkii/botania/api/mana/spark/ManaSpark;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$serverTick(Level level, BlockPos worldPosition, BlockState state, TerrestrialAgglomerationPlateBlockEntity self, CallbackInfo ci, boolean removeMana, List items, SimpleContainer inv, TerrestrialAgglomerationRecipe recipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(BotaniaRecipeTypes.TERRA_PLATE_TYPE)) return;

        Optional<IOwnerBlock> d1 = self.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && level.getServer() != null) {
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(level.getServer(), d1.get().getOwner());
                if(player != null) {
                    if (!player.hasStage(recipeBlockType.stage)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
