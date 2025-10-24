package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;

import com.drmangotea.tfmg.blocks.electricity.polarizer.PolarizerBlockEntity;
import com.drmangotea.tfmg.recipes.polarizing.PolarizingRecipe;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = PolarizerBlockEntity.class, remap = false)
public class PolarizerBlockEntityMixin {

    private final PolarizerBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack item, CallbackInfoReturnable<Optional<PolarizingRecipe>> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), thisBlockEntity));
    }
}
