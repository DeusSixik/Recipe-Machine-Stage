package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.addons.the_factory_must_grow;

import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.recipes.CastingRecipe;
import com.drmangotea.tfmg.registry.TFMGRecipeTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CastingBasinBlockEntity.class)
public abstract class CastingBasinBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    public CastingRecipe recipe;

    @Shadow
    protected abstract Object getRecipeCacheKey();

    @Shadow
    public SmartInventory inventory;

    @Shadow
    public FluidTank tank;

    protected CastingBasinBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    public void findRecipe() {
        this.recipe = null;

        for(RecipeHolder<? extends Recipe<?>> recipe1 : RMSUtils.filterRecipes(RecipeFinder.get(this.getRecipeCacheKey(), this.level, RecipeConditions.isOfType(TFMGRecipeTypes.CASTING.getType())), RMSUtils.getBlockOwner(this))) {
            CastingRecipe testedRecipe = (CastingRecipe)recipe1.value();
            if (testedRecipe.getIngrenient().test(this.tank.getFluid()) && this.inventory.isEmpty()) {
                this.recipe = testedRecipe;
                return;
            }
        }

    }
}
