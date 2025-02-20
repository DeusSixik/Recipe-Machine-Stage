package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;

import com.drmangotea.tfmg.blocks.machines.metal_processing.blast_furnace.BlastFurnaceOutputBlockEntity;
import com.drmangotea.tfmg.recipes.industrial_blasting.IndustrialBlastingRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = BlastFurnaceOutputBlockEntity.class, remap = false)
public class BlastFurnaceOutputBlockEntityMixin {

    private final BlastFurnaceOutputBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);


    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public <T> T sdm$tick(Optional<T> instance) {
        T value = instance.get();

        if(value instanceof IndustrialBlastingRecipe recipe) {
            return (T) RecipeStagesUtil.checkRecipe(recipe, thisBlockEntity);
        }

        return value;
    }
}
