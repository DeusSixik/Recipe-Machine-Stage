package net.sdm.recipemachinestage.mixin.integration.enchanted_witch;

import favouriteless.enchanted.common.blocks.entity.DistilleryBlockEntity;
import favouriteless.enchanted.common.recipes.DistillingRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DistilleryBlockEntity.class, remap = false)
public class DistilleryBlockEntityMixin {

    private DistilleryBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "canDistill", at = @At("HEAD"), cancellable = true)
    private void sdm$canDistill(DistillingRecipe recipe, CallbackInfoReturnable<Boolean> cir) {
        if(!RecipeStagesUtil.canRecipeOnBlockEntity(thisEntity, recipe))
            cir.setReturnValue(false);
    }
}
