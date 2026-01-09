package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;


import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlockEntity;
import com.drmangotea.tfmg.recipes.DistillationRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DistillationControllerBlockEntity.class, remap = false)
public class DistillationControllerBlockEntityMixin {

    private final DistillationControllerBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true)
    public void sdm$getMatchingRecipes(CallbackInfoReturnable<DistillationRecipe> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), thisBlockEntity));
    }
}
