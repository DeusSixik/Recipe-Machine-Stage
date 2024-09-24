package net.sdm.recipemachinestage.mixin.integration.embers;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.IgnemReactorBlockEntity;
import com.rekindled.embers.blockentity.MelterBottomBlockEntity;
import com.rekindled.embers.blockentity.MelterTopBlockEntity;
import com.rekindled.embers.recipe.IEmberActivationRecipe;
import com.rekindled.embers.util.Misc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
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

import java.util.Optional;

@Mixin(value = MelterBottomBlockEntity.class, remap = false)
public class MelterBottomBlockEntityMixin {


    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lcom/rekindled/embers/util/Misc;getRecipe(Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/Recipe;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$serverTick(Level level, BlockPos pos, BlockState state, MelterBottomBlockEntity blockEntity, CallbackInfo ci, MelterTopBlockEntity top, double emberCost, boolean cancel, RecipeWrapper wrapper){
        if(!StageContainer.hasRecipes(RegistryManager.MELTING.get())) return;
        Recipe recipe = Misc.getRecipe(blockEntity.cachedRecipe, (RecipeType) RegistryManager.MELTING.get(), wrapper, level);

        BlockEntity entity = level.getBlockEntity(pos);

        Optional<IOwnerBlock> d1 = entity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent()) {

            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(level.getServer(), ownerBlock.getOwner());
            if(player != null) {
                RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if (recipeBlockType != null && !player.hasStage(recipeBlockType.stage)) {
                    blockEntity.cachedRecipe = null;
                    ci.cancel();
                }
            }
        }
    }
}
