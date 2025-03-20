package net.sdm.recipemachinestage.mixin.integration.avaritia;

import committee.nova.mods.avaritia.common.crafting.recipe.CompressorRecipe;
import committee.nova.mods.avaritia.common.tile.CompressorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.patches.avaritia.ICompressorPatchAvartia;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(CompressorTile.class)
public class CompressorTileMixin implements ICompressorPatchAvartia {

    @Shadow private CompressorRecipe recipe;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", shift = At.Shift.AFTER))
    private static void sdm$tick(Level level, BlockPos pos, BlockState state, CompressorTile tile, CallbackInfo ci) {
        if(tile instanceof ICompressorPatchAvartia avartia) {
            avartia.sdm$setRecipeAvaritia(RecipeStagesUtil.checkRecipe(avartia.sdm$getRecipeAvaritia(), tile));
        }
    }

    @Override
    public CompressorRecipe sdm$getRecipeAvaritia() {
        return recipe;
    }

    @Override
    public void sdm$setRecipeAvaritia(CompressorRecipe recipeAvaritia) {
        this.recipe = recipeAvaritia;
    }
}
