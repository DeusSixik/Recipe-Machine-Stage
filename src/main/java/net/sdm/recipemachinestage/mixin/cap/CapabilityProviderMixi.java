package net.sdm.recipemachinestage.mixin.cap;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.util.LazyOptional;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.BlockOwnerCapability;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.capability.IOwnerableSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(value = CapabilityProvider.class, remap = false)
public class CapabilityProviderMixi<B extends ICapabilityProviderImpl<B>> implements IOwnerableSupport {


    private BlockOwnerCapability capability = new BlockOwnerCapability();
    private LazyOptional<IOwnerBlock> OWNER_BLOCK = LazyOptional.of(() -> capability);

    @Shadow @Final private @NotNull Class<B> baseClass;

    @Shadow private @Nullable CapabilityDispatcher capabilities;

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true)
    public <T> void sdm$getCapability(@NotNull Capability<T> cap, @Nullable Direction side, CallbackInfoReturnable<LazyOptional<T>> cir){
        if(recipe_machine_stage$isSupported()) {
            if(cap == SupportBlockData.BLOCK_OWNER) {
                cir.setReturnValue(OWNER_BLOCK.cast());
            }
        }
    }

    @Inject(method = "invalidateCaps", at = @At("TAIL"))
    public void sdm$invalidateCaps(CallbackInfo ci){
        if(recipe_machine_stage$isSupported()) {
            OWNER_BLOCK.invalidate();
        }
    }

    @Override
    public boolean recipe_machine_stage$isSupported() {
        return Objects.equals(baseClass, BlockEntity.class);
    }

    @Override
    public void recipe_machine_stage$setOwner(UUID owner) {
        this.capability.setOwner(owner);
    }

    @Override
    public UUID recipe_machine_stage$getOwner() {
        return this.capability.getOwner();
    }

    @Override
    public void recipe_machine_stage$setOwnerCapability(BlockOwnerCapability owner) {
        this.capability = owner;
    }

    @Override
    public BlockOwnerCapability recipe_machine_stage$getBlockOwnerCapability() {
        return capability;
    }
}
