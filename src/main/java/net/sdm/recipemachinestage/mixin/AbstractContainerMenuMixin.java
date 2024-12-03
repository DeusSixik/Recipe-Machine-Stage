package net.sdm.recipemachinestage.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.api.IContainerMenuSync;
import net.sdm.recipemachinestage.api.IRestrictedContainer;
import net.sdm.recipemachinestage.network.SyncStageForContainerC2S;
import net.sdm.recipemachinestage.network.SyncStageForContainerS2C;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements IContainerMenuSync {


    @Shadow protected abstract DataSlot addDataSlot(DataSlot p_38896_);

    @Shadow @Final public int containerId;
    @Unique
    private AbstractContainerMenu recipe_machine_stage$thisContainer = RecipeStagesUtil.cast(this);

    @Unique
    private String recipe_machine_stage$currentStage;

    @Inject(method = "doClick", at = @At(value = "HEAD"), cancellable = true)
    public void sdm$doClick(int i, int p_150432_, ClickType p_150433_, Player player, CallbackInfo ci) {
        if (recipe_machine_stage$thisContainer instanceof IRestrictedContainer restrictedContainer && restrictedContainer.recipe_machine_stage$getOutputSlots().contains(i)) {
            if(sdm$getStage().isEmpty()) return;

            if(PlayerHelper.hasStage(player, sdm$getStage())) return;
            ci.cancel();
        }
    }



    @Override
    public DataSlot sdm$addDataSlot(DataSlot p_38896_) {
        return addDataSlot(p_38896_);
    }

    @Override
    public void sdm$syncStage(Player player, String stage, boolean packet) {
        this.recipe_machine_stage$currentStage = stage;
        if(player.level().isClientSide) {
            RecipeMachineStage.Network.sendToServer(new SyncStageForContainerC2S(containerId, stage));
        } else {
            RecipeMachineStage.Network.sendToClient(new SyncStageForContainerS2C(containerId, stage), (ServerPlayer) player);
        }
    }

    @Override
    public String sdm$getStage() {
        return recipe_machine_stage$currentStage != null ? recipe_machine_stage$currentStage : "";
    }

    @Override
    public void sdm$setStage(String stage) {
        this.recipe_machine_stage$currentStage = stage;
    }
}
