package net.sdm.recipemachinestage.mixin.integration.mob_grinding_utils;

import mob_grinding_utils.recipe.SolidifyRecipe;
import mob_grinding_utils.tile.TileEntityXPSolidifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Deprecated
@Mixin(value = TileEntityXPSolidifier.class, remap = false)
public abstract class TileEntityXPSolidifierMixin {

    @Shadow private SolidifyRecipe currentRecipe;
//    private static TileEntityXPSolidifier thisTile;
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    private static <T extends BlockEntity> void sdm$tickPre(Level level, BlockPos worldPosition, BlockState blockState, T t, CallbackInfo ci) {
//        if (t instanceof TileEntityXPSolidifier tile) {
//            thisTile = tile;
//        }
//    }
//
//    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lmob_grinding_utils/tile/TileEntityXPSolidifier;getRecipeForMould(Lnet/minecraft/world/item/ItemStack;)Lmob_grinding_utils/recipe/SolidifyRecipe;", shift = At.Shift.AFTER))
//    private static <T extends BlockEntity> void sdm$tick(Level level, BlockPos worldPosition, BlockState blockState, T t, CallbackInfo ci) {
//        if (thisTile instanceof TileEntityXPSolidifierAccessor accessor) {
//            accessor.setCurrentRecipe(RecipeStagesUtil.checkRecipe(accessor.getCurrentRecipe(), thisTile));
//        }
//    }
//
//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lmob_grinding_utils/recipe/SolidifyRecipe;matches(Lnet/minecraft/world/item/ItemStack;)Z"))
//    private static boolean sdm$tick$matches(SolidifyRecipe instance, ItemStack input) {
//        return RecipeStagesUtil.checkRecipeOptional(instance, thisTile).isEmpty() ||  instance.matches(input);
//    }

//    @Inject(method = "hasMould", at = @At("HEAD"), cancellable = true)
//    public void sdm$hasMould(CallbackInfoReturnable<Boolean> cir) {
//        if(RecipeStagesUtil.checkRecipe(currentRecipe, RecipeStagesUtil.cast(this)) == null) {
//            System.out.println("Invoking !");
//            cir.setReturnValue(false);
//        }
//    }

//    @Shadow protected abstract boolean hasMould();
//
//    @Redirect(method = "canOperate", at = @At(value = "INVOKE", target = "Lmob_grinding_utils/tile/TileEntityXPSolidifier;hasMould()Z"))
//    public boolean sdm$canOperate(TileEntityXPSolidifier instance) {
//        return RecipeStagesUtil.checkRecipe(currentRecipe, RecipeStagesUtil.cast(this)) != null && hasMould();
//    }
}
