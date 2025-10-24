package net.sdm.recipemachinestage.mixin.integration.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.crafting.IReprocessorRecipe;
import com.blakebr0.mysticalagriculture.tileentity.ReprocessorTileEntity;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ReprocessorTileEntity.class, remap = false)
public class ReprocessorTileEntityMixin {

    @Inject(method = "getActiveRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getActiveRecipe(CallbackInfoReturnable<IReprocessorRecipe> cir){
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), RecipeStagesUtil.cast(this)));
    }
}
