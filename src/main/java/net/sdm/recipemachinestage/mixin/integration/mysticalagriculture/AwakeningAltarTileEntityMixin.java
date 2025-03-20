package net.sdm.recipemachinestage.mixin.integration.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import com.blakebr0.mysticalagriculture.tileentity.AwakeningAltarTileEntity;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AwakeningAltarTileEntity.class, remap = false)
public class AwakeningAltarTileEntityMixin {

    @Inject(method = "getActiveRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getActiveRecipe(CallbackInfoReturnable<IAwakeningRecipe> cir){
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), RecipeStagesUtil.cast(this)));
    }
}
