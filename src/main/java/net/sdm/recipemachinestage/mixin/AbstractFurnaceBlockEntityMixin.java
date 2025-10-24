package net.sdm.recipemachinestage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getMaxStackSize()I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$serverTick(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo ci, boolean flag, boolean flag1, ItemStack itemstack, boolean flag2, boolean flag3, Recipe recipe){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty()) return;

        Optional<IOwnerBlock> d1 = entity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent()) {
           IOwnerBlock ownerBlock = d1.get();
           PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(level.getServer(), ownerBlock.getOwner());
           if(player != null && recipe != null) {
               RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
               if(recipeBlockType != null && !player.hasStage(recipeBlockType.stage)) {
                   ci.cancel();
               }
           }
       }
    }
}
