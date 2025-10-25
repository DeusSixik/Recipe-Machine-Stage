package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperCommon;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperDummy;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(MultiblockBEHelperDummy.class)
public abstract class MultiblockBEHelperDummyMixin<State extends IMultiblockState>
        extends MultiblockBEHelperCommon<State>
        implements IMultiblockBEHelperDummy<State>, ImmersiveOwnerProvider {

    @Shadow
    @Nullable
    protected abstract IMultiblockBEHelperMaster<State> getIMasterHelper();

    protected MultiblockBEHelperDummyMixin(BlockEntity be, MultiblockRegistration<State> multiblock, BlockState state) {
        super(be, multiblock, state);
    }

//    @Inject(method = "getState", at = @At("RETURN"))
//    public void bts$getState(CallbackInfoReturnable<State> cir)
//    {
//        final State ret = cir.getReturnValue();
//        if(ret == null) return;
//        ImmersiveOwnerProvider.sdm$setOwner(ret, sdm$getOwner());
//
//    }
//
//    @Inject(method = "getMasterHelper(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lblusunrize/immersiveengineering/api/multiblocks/blocks/env/IMultiblockBEHelperMaster;", at = @At("RETURN"))
//    private void bts$getMasterHelper(BlockEntity beAtMasterPos, CallbackInfoReturnable<IMultiblockBEHelperMaster<State>> cir) {
//        final IMultiblockBEHelperMaster<State> getting = cir.getReturnValue();
//        if(getting == null) return;
//        ImmersiveOwnerProvider.sdm$setOwner(getting, sdm$getOwner());
//    }

    @Override
    public void sdm$setOwner(UUID ownerId) {
        final IMultiblockBEHelperMaster<State> master = getIMasterHelper();
        if(master != null)
            ImmersiveOwnerProvider.sdm$setOwner(master, ownerId);
    }
}
