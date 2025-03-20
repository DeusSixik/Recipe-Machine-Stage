package net.sdm.recipemachinestage.mixin.integration.ice_and_fire;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = TileEntityDragonforge.class, remap = false)
public abstract class TileEntityDragonForgeMixin extends BaseContainerBlockEntity {


    protected TileEntityDragonForgeMixin(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }

    @Inject(method = "getCurrentRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getCurrentRecipe(CallbackInfoReturnable<Optional<DragonForgeRecipe>> cir) {
        Optional<DragonForgeRecipe> oRecipe = cir.getReturnValue();

        cir.setReturnValue(RecipeStagesUtil.checkRecipe(oRecipe, this));
    }

    @Inject(method = "getRecipes", at = @At("RETURN"), cancellable = true)
    public void getRecipes(CallbackInfoReturnable<List<DragonForgeRecipe>> cir) {
        cir.setReturnValue(RecipeStagesUtil.checkRecipe(cir.getReturnValue(), this));
    }
}
