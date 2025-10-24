package net.sdm.recipemachinestage.mixin.integration.create;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = CrushingWheelBlock.class, remap = true)
public class CrushingWheelBlockMixin {

    private CrushingWheelBlock thisBlock = RecipeStagesUtil.cast(this);


    @Inject(method = "updateControllers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1, shift = At.Shift.AFTER))
    public void sdm$updateControllersFirst(BlockState state, Level world, BlockPos pos, Direction side, CallbackInfo ci){
        BlockPos controllerPos = pos.relative(side);
        BlockEntity entity = world.getBlockEntity(controllerPos);

        if(entity == null) return;

        Optional<IOwnerBlock> f1 = entity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        CrushingWheelBlockEntity be = (CrushingWheelBlockEntity)world.getBlockEntity(pos);

        if(be == null) return;

        Optional<IOwnerBlock> d1 = be.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if(d1.isPresent() && f1.isPresent()) {
            f1.get().setOwner(d1.get().getOwner());
        }
    }

    @Inject(method = "updateControllers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2, shift = At.Shift.AFTER))
    public void sdm$updateControllersSecond(BlockState state, Level world, BlockPos pos, Direction side, CallbackInfo ci){
        BlockPos controllerPos = pos.relative(side);
        BlockEntity entity = world.getBlockEntity(controllerPos);

        if(entity == null) return;

        Optional<IOwnerBlock> f1 = entity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        CrushingWheelBlockEntity be = (CrushingWheelBlockEntity)world.getBlockEntity(pos);

        if(be == null) return;

        Optional<IOwnerBlock> d1 = be.getCapability(RMSCapability.BLOCK_OWNER).resolve();
        if(d1.isPresent() && f1.isPresent()) {
            f1.get().setOwner(d1.get().getOwner());
        }
    }
}
