package net.sdm.recipemachinestage.mixin.integration.enderio;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(value = CraftingMachineTaskHost.class, remap = false)
public class CraftingMachineTaskHostMixin {

    @Unique
    private EnderBlockEntity sdm$blockEntity;
    @Inject(method = "<init>", at = @At("RETURN"))
    public void sdm$construct(EnderBlockEntity blockEntity, Supplier canAcceptNewTask, RecipeType recipeType, Container container, CraftingMachineTaskHost.ICraftingMachineTaskFactory taskFactory, CallbackInfo ci) {
        this.sdm$blockEntity = blockEntity;
    }

    @Inject(method = "getNewTask()Lcom/enderio/machines/common/blockentity/task/CraftingMachineTask;", at = @At("RETURN"), cancellable = true)
    public<R extends MachineRecipe<C>, C extends Container> void sdm$getNewTask(CallbackInfoReturnable<CraftingMachineTask<R, C>> cir) {
        CraftingMachineTask<R, C> task = cir.getReturnValue();
        if(task == null) return;

        R recipe = task.getRecipe();

        if(recipe == null) return;

        Optional<IOwnerBlock> optionalOwnerBlock = sdm$blockEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (optionalOwnerBlock.isPresent() && sdm$blockEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = optionalOwnerBlock.get();

            PlayerHelper.RMSStagePlayerData playerData = PlayerHelper.getPlayerByGameProfile(sdm$blockEntity.getLevel().getServer(), ownerBlock.getOwner());
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());

            if(recipeBlockType == null) return;
            if(playerData == null) return;

            if (!playerData.hasStage(recipeBlockType.stage)) {
                cir.setReturnValue(null);
            }

        }

    }
}
