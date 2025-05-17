package net.sdm.recipemachinestage.mixin.integration.biomancy;

import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.block.digester.DigesterStateData;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
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
        value = {DigesterBlockEntity.class},
        remap = false
)
public abstract class DigesterBlockEntityMixin extends MachineBlockEntity<DigestingRecipe, DigesterStateData> {
    protected DigesterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = {"craftRecipe(Lcom/github/elenterius/biomancy/crafting/recipe/DigestingRecipe;Lnet/minecraft/world/level/Level;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void sdm$craftRecipe(DigestingRecipe recipeToCraft, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (!RecipeStagesUtil.canRecipeOnBlockEntity(this, recipeToCraft)) {
            cir.setReturnValue(false);
        }

    }
}
