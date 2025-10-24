package net.sdm.recipemachinestage.mixin.integration.summoningrituals;

import com.almostreliable.summoningrituals.altar.AltarBlockEntity;
import com.almostreliable.summoningrituals.recipe.AltarRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AltarBlockEntity.class, remap = false)
public class AltarBlockEntityMixin {

    private final AltarBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$findRecipe(CallbackInfoReturnable<AltarRecipe> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), thisBlockEntity));
    }
}
