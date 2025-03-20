package net.sdm.recipemachinestage.mixin.integration.mekanism.mekanism_science;

import com.fxd927.mekanismscience.api.recipes.RadiationIrradiatingRecipe;
import com.fxd927.mekanismscience.common.tile.machine.TileEntityRadiationIrradiator;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityRadiationIrradiator.class, remap = false)
public class TileEntityRadiationIrradiatorMixin {

    private final TileEntityRadiationIrradiator thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipe(I)Lcom/fxd927/mekanismscience/api/recipes/RadiationIrradiatingRecipe;", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(int cacheIndex, CallbackInfoReturnable<RadiationIrradiatingRecipe> cir) {
        RadiationIrradiatingRecipe rRecipe = cir.getReturnValue();

        if(rRecipe == null) return;

        cir.setReturnValue(RecipeStagesUtil.checkRecipe(rRecipe, thisBlockEntity));
    }
}
