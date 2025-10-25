package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperMaster;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MultiblockBEHelperMaster.class)
public abstract class MultiblockBEHelperMasterMixin<State extends IMultiblockState>
        extends MultiblockBEHelperCommon<State>
        implements ImmersiveOwnerProvider {

    @Shadow
    @Final
    private State state;

    protected MultiblockBEHelperMasterMixin(BlockEntity be, MultiblockRegistration<State> multiblock, BlockState state) {
        super(be, multiblock, state);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("RETURN"))
    public void bts$load(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
       if(tag.contains(ImmersiveOwnerProvider.KEY)) {
           sdm$setOwner(tag.getUUID(ImmersiveOwnerProvider.KEY));
       }
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    public void bts$save(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        final UUID data = sdm$getOwner();
        if(data == null) return;

        tag.putUUID(ImmersiveOwnerProvider.KEY, data);
    }

    @Override
    public void sdm$setOwner(UUID ownerId) {
        ImmersiveOwnerProvider.super.sdm$setOwner(ownerId);
        ImmersiveOwnerProvider.sdm$setOwner(state, ownerId);
    }
}
