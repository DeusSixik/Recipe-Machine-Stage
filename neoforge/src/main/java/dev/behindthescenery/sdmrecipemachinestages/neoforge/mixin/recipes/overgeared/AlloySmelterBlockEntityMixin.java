package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.overgeared;

import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.overgeared.OvergearedRecipeSupport;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.stirdrem.overgeared.block.entity.AlloySmelterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Applies recipe-stage restrictions to the Overgeared alloy smelter.
 */
@Mixin(AlloySmelterBlockEntity.class)
public abstract class AlloySmelterBlockEntityMixin {

    /**
     * Removes a shaped or shapeless alloy recipe when the block owner lacks its stage.
     *
     * @param cir the current untyped recipe-holder lookup result, which is replaced when access is denied.
     */
    @Inject(method = "getCurrentRecipe", at = @At("RETURN"), cancellable = true)
    private void bts$filterAlloyRecipe(CallbackInfoReturnable<Optional<RecipeHolder<?>>> cir) {
        final BlockEntity blockEntity = (BlockEntity) (Object) this;
        cir.setReturnValue(OvergearedRecipeSupport.filter(
                cir.getReturnValue(),
                holder -> RMSUtils.canProcess(blockEntity, holder)
        ));
    }
}
