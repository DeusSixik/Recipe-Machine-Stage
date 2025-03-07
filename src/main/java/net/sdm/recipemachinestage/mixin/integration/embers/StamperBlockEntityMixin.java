package net.sdm.recipemachinestage.mixin.integration.embers;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.StampBaseBlockEntity;
import com.rekindled.embers.blockentity.StamperBlockEntity;
import com.rekindled.embers.recipe.StampingContext;
import com.rekindled.embers.util.Misc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
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

@Mixin(value = StamperBlockEntity.class, remap = false)
public class StamperBlockEntityMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lcom/rekindled/embers/util/Misc;getRecipe(Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/Recipe;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$serverTick(Level level, BlockPos pos, BlockState state, StamperBlockEntity blockEntity, CallbackInfo ci, StampBaseBlockEntity stamp, IFluidHandler handler, StampingContext context){
        if(!StageContainer.hasRecipes(RegistryManager.ALCHEMY.get())) return;
        Recipe recipe = Misc.getRecipe(blockEntity.cachedRecipe, (RecipeType) RegistryManager.ALCHEMY.get(), context, level);

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
