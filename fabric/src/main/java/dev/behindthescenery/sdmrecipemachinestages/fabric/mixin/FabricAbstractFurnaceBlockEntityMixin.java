package dev.behindthescenery.sdmrecipemachinestages.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractFurnaceBlockEntity.class)
public abstract class FabricAbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {


    @Shadow
    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipeHolder,
                                   NonNullList<ItemStack> nonNullList, int i) { return  false;}

    protected FabricAbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * @author Sixik
     * @reason Path for recipes with count
     */
    @Overwrite
    private static boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize) {
        if (recipe != null && canBurn(registryAccess, recipe, inventory, maxStackSize)) {
            final AbstractCookingRecipe r1 = ((AbstractCookingRecipe)recipe.value());
            final ItemStack inputItem = inventory.get(0);
            final ItemStack outputItem = inventory.get(2);
            final ItemStack resultItem = r1.getResultItem(registryAccess);

            final int out_max_stack_size = outputItem.getMaxStackSize();

            if (outputItem.isEmpty()) {
                inventory.set(2, resultItem.copy());
            } else if (ItemStack.isSameItemSameComponents(outputItem, resultItem)) {
                final int resultSize = resultItem.getCount();
                if(outputItem.getCount() + resultSize <= out_max_stack_size) {
                    outputItem.grow(resultSize);
                } else return false;
            }

            if (inputItem.is(Blocks.WET_SPONGE.asItem()) && !inventory.get(1).isEmpty() && inventory.get(1).is(Items.BUCKET)) {
                inventory.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            int count = 1;
            for (final ItemStack item : r1.getIngredients().get(0).getItems()) {
                count = Math.max(count, item.getCount());
            }

            inputItem.shrink(count);
            return true;
        } else {
            return false;
        }
    }
}