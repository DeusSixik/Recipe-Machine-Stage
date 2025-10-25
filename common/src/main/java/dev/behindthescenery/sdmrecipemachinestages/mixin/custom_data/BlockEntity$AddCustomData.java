package dev.behindthescenery.sdmrecipemachinestages.mixin.custom_data;

import com.llamalad7.mixinextras.sugar.Local;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockEntityCustomData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.CustomData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public class BlockEntity$AddCustomData implements BlockEntityCustomData {

    @Unique
    protected BlockOwnerData bts$blockOwnerData = new BlockOwnerData();

    @Inject(method = "saveWithoutMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    protected void bts$saveAdditional(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir, @Local CompoundTag compoundTag) {
        compoundTag.put(BlockEntityCustomData.CUSTOM_KEY, bts$save());
    }

    @Inject(method = "saveCustomOnly", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    protected void bts$saveCustomOnly(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir, @Local CompoundTag compoundTag) {
        compoundTag.put(BlockEntityCustomData.CUSTOM_KEY, bts$save());
    }

    @Inject(method = "loadWithComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    protected void bts$loadWithComponents(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        if(compoundTag.contains(BlockEntityCustomData.CUSTOM_KEY)) {
            bts$load(compoundTag.get(BlockEntityCustomData.CUSTOM_KEY));
        }
    }

    @Inject(method = "loadCustomOnly", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    protected void bts$loadCustomOnly(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        if(compoundTag.contains(BlockEntityCustomData.CUSTOM_KEY)) {
            bts$load(compoundTag.get(BlockEntityCustomData.CUSTOM_KEY));
        }
    }

    @Override
    public CustomData sdm$getCustomData() {
        return bts$blockOwnerData;
    }

    @Override
    public Tag bts$save() {
        final Tag tag = sdm$getCustomData().save();
        bts$onSave(tag);
        return tag;
    }

    @Override
    public void bts$load(Tag tag) {
        sdm$getCustomData().load(tag);
        bts$onLoad(tag);
    }
}
