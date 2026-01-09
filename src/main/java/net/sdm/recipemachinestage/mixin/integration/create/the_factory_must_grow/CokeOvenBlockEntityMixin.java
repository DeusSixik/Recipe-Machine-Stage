package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.recipes.CokingRecipe;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = CokeOvenBlockEntity.class, remap = false)
public class CokeOvenBlockEntityMixin {

    private final CokeOvenBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    @Redirect(method = "tickRecipe", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public <T> T sdm$tick(Optional<T> instance) {
        T value = instance.get();

        if(value instanceof CokingRecipe recipe) {
            return (T) RecipeStagesUtil.checkRecipe(recipe, thisBlockEntity);
        }

        return value;
    }
}
