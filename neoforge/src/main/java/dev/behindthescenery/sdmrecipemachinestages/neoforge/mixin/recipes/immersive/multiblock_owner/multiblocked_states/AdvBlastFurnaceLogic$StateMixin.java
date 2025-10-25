package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner.multiblocked_states;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AdvBlastFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.BlastFurnaceLogic;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(AdvBlastFurnaceLogic.State.class)
public class AdvBlastFurnaceLogic$StateMixin implements ImmersiveOwnerProvider {

    @Shadow
    @Final
    private BlastFurnaceLogic.State innerState;

    @Override
    public void sdm$setOwner(UUID ownerId) {
        ((ImmersiveOwnerProvider)innerState).sdm$setOwner(ownerId);
    }

    @Override
    public UUID sdm$getOwner() {
        return ((ImmersiveOwnerProvider)innerState).sdm$getOwner();
    }
}
