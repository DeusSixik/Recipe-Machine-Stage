package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveMultiblockBlockEntityCommonPatch;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MultiblockBlockEntityDummy.class)
public abstract class MultiblockBlockEntityDummyMixin<State extends IMultiblockState>
        extends BlockEntity
        implements IModelOffsetProvider, IMultiblockBE<State>, ImmersiveMultiblockBlockEntityCommonPatch {

    @Shadow
    @Final
    private IMultiblockBEHelperDummy<State> helper;

    protected MultiblockBlockEntityDummyMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci)
    {
        sdm$im_setOwner(BlockOwnerData.getOrThrow(this));
    }

    @Override
    public void sdm$im_setOwner(UUID uuid) {
        ImmersiveOwnerProvider.sdm$setOwner(helper, uuid);
    }
}
