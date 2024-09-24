package net.sdm.recipemachinestage.mixin.integration.embers;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MiniBoilerBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeTopBlockEntity;
import com.rekindled.embers.recipe.FluidHandlerContext;
import com.rekindled.embers.recipe.MixingContext;
import com.rekindled.embers.util.Misc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(value = MiniBoilerBlockEntity.class, remap = false)
public class MiniBoilerBlockEntityMixin {

    private MiniBoilerBlockEntity blockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "boil", at = @At(value = "INVOKE", target = "Lcom/rekindled/embers/util/Misc;getRecipe(Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/Recipe;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void sdm$boid(double heat, CallbackInfo ci, FluidStack fluid, FluidHandlerContext context){
        if(!StageContainer.hasRecipes(RegistryManager.BOILING.get())) return;
        Recipe recipe = Misc.getRecipe(blockEntity.cachedRecipe, (RecipeType) RegistryManager.BOILING.get(), context, blockEntity.getLevel());

        Optional<IOwnerBlock> d1 = blockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent()) {

            IOwnerBlock ownerBlock = d1.get();
            PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(blockEntity.getLevel().getServer(), ownerBlock.getOwner());
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
