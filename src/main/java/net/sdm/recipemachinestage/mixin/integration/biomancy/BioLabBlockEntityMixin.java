package net.sdm.recipemachinestage.mixin.integration.biomancy;

import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.block.biolab.BioLabStateData;
import com.github.elenterius.biomancy.crafting.recipe.BioBrewingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = {BioLabBlockEntity.class},
        remap = false
)
public abstract class BioLabBlockEntityMixin extends MachineBlockEntity<BioBrewingRecipe, BioLabStateData> {
    protected BioLabBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = {"craftRecipe(Lcom/github/elenterius/biomancy/crafting/recipe/BioBrewingRecipe;Lnet/minecraft/world/level/Level;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void sdm$craftRecipe(BioBrewingRecipe recipeToCraft, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (!RecipeStagesUtil.canRecipeOnBlockEntity(this, recipeToCraft)) {
            cir.setReturnValue(false);
        }

    }
}
