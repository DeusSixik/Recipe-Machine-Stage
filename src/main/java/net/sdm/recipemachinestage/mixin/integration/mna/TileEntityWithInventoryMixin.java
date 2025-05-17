package net.sdm.recipemachinestage.mixin.integration.mna;

import com.mna.api.blocks.tile.TileEntityWithInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityWithInventory.class, remap = false)
public abstract class TileEntityWithInventoryMixin extends BlockEntity {

    public TileEntityWithInventoryMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }


    @Inject(method = "saveAdditional", at = @At("RETURN"))
    public void sdm$saveAdditional(CompoundTag compound, CallbackInfo ci) {
        if (this.getCapabilities() != null) {
            compound.put("ForgeCaps", this.serializeCaps());
        }
    }
}
