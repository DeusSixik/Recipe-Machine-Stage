package net.sdm.recipemachinestage.mixin.integration.overgeared;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.compat.overgeared.OvergearedRecipeSupport;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.stirdrem.overgeared.block.entity.AbstractSmithingAnvilBlockEntity;
import net.stirdrem.overgeared.recipe.ForgingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Applies recipe-stage restrictions to Overgeared smithing anvils.
 */
@Mixin(AbstractSmithingAnvilBlockEntity.class)
public abstract class AbstractSmithingAnvilBlockEntityMixin {

    /**
     * Removes a forging recipe selected for an anvil whose owner lacks the required stage.
     *
     * @param cir the current forging recipe lookup result, which is replaced when access is denied.
     */
    @Inject(method = "getCurrentRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private void recipeMachineStage$filterForgingRecipe(
            CallbackInfoReturnable<Optional<ForgingRecipe>> cir
    ) {
        final BlockEntity blockEntity = (BlockEntity) (Object) this;
        cir.setReturnValue(OvergearedRecipeSupport.filter(
                cir.getReturnValue(),
                recipe -> RMSUtils.canRecipe(recipe, blockEntity)
        ));
    }
}
