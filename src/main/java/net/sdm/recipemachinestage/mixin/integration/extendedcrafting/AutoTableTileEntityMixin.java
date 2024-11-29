package net.sdm.recipemachinestage.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.compat.extendercrafting.IWrappedRecipeAddition;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = AutoTableTileEntity.class, remap = false)
public class AutoTableTileEntityMixin {

    @Unique
    private final AutoTableTileEntity recipe_machine_stage$thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getActiveRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$getActiveRecipe(CallbackInfoReturnable<AutoTableTileEntity.WrappedRecipe> cir){
        AutoTableTileEntity.WrappedRecipe f = cir.getReturnValue();
        if(f != null && f instanceof IWrappedRecipeAddition recipeAddition) {

            Recipe<?> recipe = recipeAddition.recipe_machine_stage$getRecipe();

            if(!StageContainer.hasRecipes(recipe.getType())) {
                return;
            }

            Optional<IOwnerBlock> d1 = recipe_machine_stage$thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && recipe_machine_stage$thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.setReturnValue(null);
                        }
                    } else cir.setReturnValue(null);
                }
            }
        }
    }
}
