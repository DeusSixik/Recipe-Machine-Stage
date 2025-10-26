package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.applied;

import appeng.hooks.IBlockTool;
import appeng.items.tools.powered.EntropyManipulatorItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.recipes.entropy.EntropyMode;
import appeng.recipes.entropy.EntropyRecipe;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;

@Mixin(EntropyManipulatorItem.class)
public abstract class EntropyManipulatorItemMixin extends AEBasePoweredItem implements IBlockTool {

    public EntropyManipulatorItemMixin(DoubleSupplier powerCapacity, Properties props) {
        super(powerCapacity, props);
    }

    @Inject(method = "tryApplyEffect", at = @At("HEAD"))
    public void bts$tryApplyEffect(Level level, ItemStack item, BlockPos pos, Direction side, Player p, boolean tryBoth, CallbackInfoReturnable<Boolean> cir) {
        RMSMain.setBlockOwner(RMSUtils.getPlayerId(p));
    }

    /**
     * @author Sixik
     * @reason Add restriction recipe
     */
    @Overwrite
    @SuppressWarnings("removal")
    private static @Nullable EntropyRecipe findRecipe(Level level, EntropyMode mode, BlockState blockState, FluidState fluidState) {
        for(final RecipeHolder<EntropyRecipe> holder : RMSUtils.filterRecipes(level.getRecipeManager().getAllRecipesFor(EntropyRecipe.TYPE), RMSMain.getBlockOwner())) {
            final EntropyRecipe recipe = holder.value();
            if (recipe.matches(mode, blockState, fluidState)) {
                return recipe;
            }
        }

        return null;
    }
}
