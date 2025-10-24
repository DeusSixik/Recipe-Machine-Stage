package net.sdm.recipemachinestage.mixin.integration.create.create_craft_addition;

import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlockEntity;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
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

@Mixin(value = TeslaCoilBlockEntity.class, remap = false)
public class TeslaCoilBlockEntityMixin {

    private TeslaCoilBlockEntity thisEntity = RecipeStagesUtil.cast(this);


    @Inject(method = "find", at = @At("RETURN"), cancellable = true)
    private void sdm$find(RecipeWrapper wrapper, Level world, CallbackInfoReturnable<Optional<ChargingRecipe>> cir) {
        Optional<ChargingRecipe> optional = cir.getReturnValue();

        if(optional.isPresent() ) {
            ChargingRecipe recipe = optional.get();

//            if(!StageContainer.hasRecipes(recipe.getType())) return;

            Optional<IOwnerBlock> d1 = thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
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
