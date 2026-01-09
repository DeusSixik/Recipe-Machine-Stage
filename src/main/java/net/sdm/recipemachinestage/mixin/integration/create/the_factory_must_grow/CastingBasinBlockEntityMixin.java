package net.sdm.recipemachinestage.mixin.integration.create.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.recipes.CastingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CastingBasinBlockEntity.class, remap = false)
public abstract class CastingBasinBlockEntityMixin extends SmartBlockEntity {

    public CastingBasinBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    protected abstract Object getRecipeCacheKey();

    @Shadow
    public CastingRecipe recipe;
    @Shadow
    public FluidTank tank;
    @Shadow
    public SmartInventory inventory;
    private CastingBasinBlockEntity thisBlockEntity = RecipeStagesUtil.cast(this);

    /**
     * @author Sixik
     * @reason
     */
    @Overwrite
    public void findRecipe() {
        this.recipe = null;

        for(Recipe<?> recipe1 : RecipeFinder.get(this.getRecipeCacheKey(), this.level, (r) -> r instanceof CastingRecipe)) {
            CastingRecipe testedRecipe = (CastingRecipe)recipe1;
            if (testedRecipe.getIngrenient().test(this.tank.getFluid()) && this.inventory.isEmpty()) {
                this.recipe = RecipeStagesUtil.checkRecipe(testedRecipe, thisBlockEntity);
                return;
            }
        }
    }
}
