package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOfferingTable;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockEntityOfferingTable.class)
public abstract class BlockEntityOfferingTableMixin extends BlockEntityImpl {

    protected BlockEntityOfferingTableMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Overwrite
    private OfferingRecipe getRecipe(ItemStack input) {
        for(RecipeHolder<OfferingRecipe> recipe : RMSUtils.filterRecipes(this.level.getRecipeManager().getRecipesFor(ModRecipes.OFFERING_TYPE, null, this.level), RMSUtils.getBlockOwner(this))) {
            if (recipe.value().input.test(input)) {
                return recipe.value();
            }
        }

        return null;
    }
}
