package net.sdm.recipemachinestage.mixin.integration.mna;

import com.mna.api.blocks.tile.TileEntityWithInventory;
import com.mna.blocks.tileentities.RuneForgeTile;
import com.mna.recipes.arcanefurnace.ArcaneFurnaceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RuneForgeTile.class, remap = false)
public abstract class RuneForgeTileMixin extends TileEntityWithInventory {

    @Shadow private ArcaneFurnaceRecipe __cachedRecipe;

    @Shadow public abstract void setChanged();

    public RuneForgeTileMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityTypeIn, pos, state, inventorySize);
    }

    @Inject(method = "tickLogic_smelt", at = @At(value = "HEAD", target = "Lcom/mna/blocks/tileentities/RuneForgeTile;cacheRecipe()Z"), cancellable = true)
    public void sdm$tickLogic_smelt(CallbackInfo ci) {
        if(__cachedRecipe == null) return;
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(__cachedRecipe.getType())) return;

        if (!RecipeStagesUtil.canRecipeOnBlockEntity(this, __cachedRecipe)) {
            ci.cancel();
            setChanged();
        }
    }
}
