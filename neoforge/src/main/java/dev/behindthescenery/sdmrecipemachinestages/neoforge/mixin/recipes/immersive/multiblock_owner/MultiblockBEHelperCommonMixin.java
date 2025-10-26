package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(MultiblockBEHelperCommon.class)
public class MultiblockBEHelperCommonMixin implements ImmersiveOwnerProvider {

    @Unique
    private UUID bts$ownerId = BlockOwnerData.EMPTY;

    @Override
    public void sdm$setOwner(UUID ownerId) {
        this.bts$ownerId = ownerId;
    }

    @Override
    public UUID sdm$getOwner() {
        return bts$ownerId;
    }
}
