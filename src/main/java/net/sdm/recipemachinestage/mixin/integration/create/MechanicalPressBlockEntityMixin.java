package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.world.item.ItemStack;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = MechanicalPressBlockEntity.class, remap = false)
public class MechanicalPressBlockEntityMixin {

    private MechanicalPressBlockEntity thisEntity = RecipeStagesUtil.cast(this);


    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack item, CallbackInfoReturnable<Optional<PressingRecipe>> cir) {
        Optional<PressingRecipe> o_recipe = cir.getReturnValue();
        if(o_recipe.isPresent()) {
            PressingRecipe recipe = o_recipe.get();

            if(!StageContainer.hasRecipes(recipe.getType())) {
                return;
            }

            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.setReturnValue(Optional.empty());
                        }
                    }
                }
            }
        }
    }
}
