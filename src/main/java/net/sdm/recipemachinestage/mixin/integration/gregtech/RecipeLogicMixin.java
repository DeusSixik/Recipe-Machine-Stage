package net.sdm.recipemachinestage.mixin.integration.gregtech;

import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = RecipeLogic.class, remap = false)
public abstract class RecipeLogicMixin {

    @Shadow @Final public IRecipeLogicMachine machine;

    @Shadow public abstract void setStatus(RecipeLogic.Status status);

    @Shadow protected int progress;

    @Shadow protected int duration;

    @Shadow private boolean isActive;

    @Inject(method = "setupRecipe", at = @At("HEAD"), cancellable = true)
    public void sdm$setupRecip(GTRecipe recipe, CallbackInfo ci) {
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipe.getType())) return;

        BlockEntity entity = machine.self().holder.self();
        Optional<IOwnerBlock> d1 = entity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && entity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType = StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if (recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(entity.getLevel().getServer(), ownerBlock.getOwner());
                if (player != null) {
                    if (!player.hasStage(recipeBlockType.stage)) {
                        setStatus(RecipeLogic.Status.IDLE);
                        progress = 0;
                        duration = 0;
                        isActive = false;
                        ci.cancel();
                    }
                }
            }
        }

    }
}
