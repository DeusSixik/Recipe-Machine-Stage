package net.sdm.recipemachinestage.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sdm.recipemachinestage.capability.IOwnerableSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {

    private BlockEntity thisBlock = (BlockEntity)(Object)this;

    @Inject(method = "load", at = @At("RETURN"))
    public void sdm$load(CompoundTag compoundTag, CallbackInfo ci){
        if(thisBlock instanceof IOwnerableSupport ownerableSupport) {
            if(compoundTag.contains("rms_owner"))
                ownerableSupport.recipe_machine_stage$getBlockOwnerCapability().read(compoundTag.getCompound("rms_owner"));
        }
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    public void sdm$saveAdditional(CompoundTag compoundTag, CallbackInfo ci){
        if(thisBlock instanceof IOwnerableSupport ownerableSupport) {
            CompoundTag nbt = new CompoundTag();
            ownerableSupport.recipe_machine_stage$getBlockOwnerCapability().write(nbt);
            compoundTag.put("rms_owner",nbt);
        }
    }
}
