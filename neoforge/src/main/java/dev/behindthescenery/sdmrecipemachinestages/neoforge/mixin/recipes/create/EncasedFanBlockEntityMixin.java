package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockEntityCustomData;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.AirCurrentPatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EncasedFanBlockEntity.class)
public abstract class EncasedFanBlockEntityMixin extends KineticBlockEntity implements IAirCurrentSource, BlockEntityCustomData {

    @Shadow
    public AirCurrent airCurrent;

    protected EncasedFanBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "read", at = @At("RETURN"))
    protected void bts$read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        ((AirCurrentPatch)airCurrent).bts$setOwnerBlock(RMSUtils.getBlockOwner(this));
    }

    @Override
    public void sdm$setOwner(UUID uuid) {
        BlockEntityCustomData.super.sdm$setOwner(uuid);
        ((AirCurrentPatch)airCurrent).bts$setOwnerBlock(uuid);
    }
}
