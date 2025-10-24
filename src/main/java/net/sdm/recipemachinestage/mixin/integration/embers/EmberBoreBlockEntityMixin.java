package net.sdm.recipemachinestage.mixin.integration.embers;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;
import com.rekindled.embers.recipe.BoringContext;
import com.rekindled.embers.recipe.IBoringRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = EmberBoreBlockEntity.class, remap = false)
public class EmberBoreBlockEntityMixin {


    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lcom/rekindled/embers/api/upgrades/UpgradeUtil;throwEvent(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/rekindled/embers/api/event/UpgradeEvent;Ljava/util/List;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void sdm$serverTick(Level level, BlockPos pos, BlockState state, EmberBoreBlockEntity blockEntity, CallbackInfo ci, boolean previousRunning, double fuelConsumption, boolean cancel, int boreTime, ResourceKey biome, BoringContext context, List recipes){
        if(!StageContainer.hasRecipes(RegistryManager.BORING.get()))  return;

        BlockEntity entity = level.getBlockEntity(pos);

        Optional<IOwnerBlock> d1 = entity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if (d1.isPresent()) {
            Iterator iterator = recipes.iterator();
            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(level.getServer(), ownerBlock.getOwner());
            if(player != null) {
                while (iterator.hasNext()) {
                    IBoringRecipe recipe = (IBoringRecipe) iterator.next();
                    RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                    if (recipeBlockType != null && !player.hasStage(recipeBlockType.stage)) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
