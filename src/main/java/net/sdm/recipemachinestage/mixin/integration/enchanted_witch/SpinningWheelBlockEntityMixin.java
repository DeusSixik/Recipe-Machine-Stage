package net.sdm.recipemachinestage.mixin.integration.enchanted_witch;

import favouriteless.enchanted.common.blocks.entity.SpinningWheelBlockEntity;
import favouriteless.enchanted.common.recipes.SpinningRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpinningWheelBlockEntity.class, remap = false)
public class SpinningWheelBlockEntityMixin {

    private SpinningWheelBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "spin", at = @At("HEAD"), cancellable = true)
    private void sdm$spin(SpinningRecipe recipe, CallbackInfo ci) {
        if(!RecipeStagesUtil.canRecipeOnBlockEntity(thisEntity, recipe))
            ci.cancel();
    }
}
