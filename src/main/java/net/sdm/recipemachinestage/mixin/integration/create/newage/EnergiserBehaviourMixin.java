package net.sdm.recipemachinestage.mixin.integration.create.newage;

import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.antarcticgardens.newage.content.energiser.EnergiserBehaviour;
import org.antarcticgardens.newage.content.energiser.EnergiserBlockEntity;
import org.antarcticgardens.newage.content.energiser.EnergisingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnergiserBehaviour.class, remap = false)
public class EnergiserBehaviourMixin {

    @Shadow protected EnergiserBlockEntity be;

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack stack, CallbackInfoReturnable<EnergisingRecipe> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), be));
    }
}
