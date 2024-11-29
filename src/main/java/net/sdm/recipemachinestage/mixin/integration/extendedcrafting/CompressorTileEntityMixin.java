package net.sdm.recipemachinestage.mixin.integration.extendedcrafting;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.extendedcrafting.api.crafting.ICompressorRecipe;
import com.blakebr0.extendedcrafting.api.crafting.IEnderCrafterRecipe;
import com.blakebr0.extendedcrafting.tileentity.CompressorTileEntity;
import com.blakebr0.extendedcrafting.tileentity.EnderCrafterTileEntity;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = CompressorTileEntity.class, remap = false)
public class CompressorTileEntityMixin {

    @Shadow @Final
    private CachedRecipe<IEnderCrafterRecipe> recipe;
    @Shadow @Final private BaseItemStackHandler inventory;
    @Unique
    private CompressorTileEntity recipe_machine_stage$thisEntity = RecipeStagesUtil.cast(this);


    @Inject(method = "getActiveRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$getActiveRecipe(CallbackInfoReturnable<ICompressorRecipe> cir){
        ICompressorRecipe recipe = cir.getReturnValue();
        if(recipe != null) {

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
                    }
                }
            }
        }
    }
}
