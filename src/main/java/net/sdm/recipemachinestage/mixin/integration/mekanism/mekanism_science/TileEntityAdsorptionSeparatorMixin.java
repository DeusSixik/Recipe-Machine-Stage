package net.sdm.recipemachinestage.mixin.integration.mekanism.mekanism_science;

import com.fxd927.mekanismscience.api.recipes.AdsorptionRecipe;
import com.fxd927.mekanismscience.common.tile.machine.TileEntityAdsorptionSeparator;
import com.fxd927.mekanismscience.common.tile.prefab.MSTileEntityProgressMachine;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.cache.CachedRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = TileEntityAdsorptionSeparator.class, remap = false)
public abstract class TileEntityAdsorptionSeparatorMixin extends MSTileEntityProgressMachine<AdsorptionRecipe> {


    protected TileEntityAdsorptionSeparatorMixin(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Inject(method = "getRecipe(I)Lcom/fxd927/mekanismscience/api/recipes/AdsorptionRecipe;", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(int cacheIndex, CallbackInfoReturnable<AdsorptionRecipe> cir) {
        AdsorptionRecipe rRecipe = cir.getReturnValue();
        if(rRecipe == null) return;

        cir.setReturnValue(RecipeStagesUtil.checkRecipe(rRecipe, this));
    }
}
