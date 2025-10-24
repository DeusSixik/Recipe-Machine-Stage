package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;

import com.drmangotea.tfmg.blocks.machines.metal_processing.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.recipes.casting.CastingRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CastingBasinBlockEntity.class, remap = false)
public class CastingBasinBlockEntityMixin {

    private CastingBasinBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true)
    public void sdm$getMatchingRecipes(CallbackInfoReturnable<CastingRecipe> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), thisBlockEntity));
    }
}
