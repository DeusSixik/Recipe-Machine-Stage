package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner.multiblocked_states;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(RedstoneControl.RSState.class)
public class RedstoneControl$RSStateMixin implements ImmersiveOwnerProvider {

    @Unique
    private UUID sdm$owner;

    @Override
    public void sdm$setOwner(UUID ownerId) {
        this.sdm$owner = ownerId;
    }

    @Override
    public UUID sdm$getOwner() {
        return sdm$owner;
    }
}
