package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveMultiblockBlockEntityCommonPatch;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(MultiblockBlockEntityMaster.class)
public class MultiblockBlockEntityMasterMixin<State extends IMultiblockState> implements ImmersiveMultiblockBlockEntityCommonPatch {
    @Shadow
    @Final
    private IMultiblockBEHelperMaster<State> helper;

    @Override
    public void sdm$im_setOwner(UUID uuid) {
        if(helper instanceof ImmersiveOwnerProvider ownerProvider) {
            ownerProvider.sdm$setOwner(uuid);
            return;
        }

        throw new RuntimeException("Can't set owner! " + helper.getClass().getName());
    }
}
