package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.overgeared;

import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.overgeared.OvergearedRecipeSupport;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
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
     * @param cir the current recipe-holder lookup result, which is replaced when access is denied.
     */
    @Inject(method = "getCurrentRecipeHolder", at = @At("RETURN"), cancellable = true)
    private void bts$filterForgingRecipe(
            CallbackInfoReturnable<Optional<RecipeHolder<ForgingRecipe>>> cir
    ) {
        final BlockEntity blockEntity = (BlockEntity) (Object) this;
        cir.setReturnValue(OvergearedRecipeSupport.filter(
                cir.getReturnValue(),
                holder -> RMSUtils.canProcess(blockEntity, holder)
        ));
    }
}
