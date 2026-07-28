package net.sdm.recipemachinestage.mixin.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ImbuementTile.class,remap = false)
public class ImbuementTileMixin {

    @Inject(method = "getRecipeNow", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipeNow(CallbackInfoReturnable<IImbuementRecipe> cir){
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), RecipeStagesUtil.cast(this)));
    }
}
