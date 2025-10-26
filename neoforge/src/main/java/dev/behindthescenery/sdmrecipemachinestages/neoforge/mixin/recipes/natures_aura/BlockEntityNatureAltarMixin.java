package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityNatureAltar;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityNatureAltar.class)
public abstract class BlockEntityNatureAltarMixin extends BlockEntityImpl{


    protected BlockEntityNatureAltarMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getRecipeForInput", at = @At("RETURN"), cancellable = true)
    private void rms$getRecipeForInput(ItemStack input, CallbackInfoReturnable<RecipeHolder<AltarRecipe>> cir) {
        final RecipeHolder<AltarRecipe> recipeHolder = cir.getReturnValue();
        if(recipeHolder == null) return;

        if(!RMSUtils.canProcess(this, recipeHolder))
            cir.setReturnValue(null);
    }
}
