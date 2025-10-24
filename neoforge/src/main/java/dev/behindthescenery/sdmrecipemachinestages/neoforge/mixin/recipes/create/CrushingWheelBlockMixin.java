package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CrushingWheelBlock.class)
public abstract class CrushingWheelBlockMixin extends RotatedPillarKineticBlock implements IBE<CrushingWheelBlockEntity> {

    protected CrushingWheelBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "updateControllers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1, shift = At.Shift.AFTER))
    public void sdm$updateControllersFirst(BlockState state, Level world, BlockPos pos, Direction side, CallbackInfo ci){
        final BlockPos controllerPos = pos.relative(side);
        final BlockEntity entity = world.getBlockEntity(controllerPos);

        if(entity == null) return;

        final CrushingWheelBlockEntity be = (CrushingWheelBlockEntity)world.getBlockEntity(pos);

        if(be == null) return;

        final UUID ownerId = BlockOwnerData.getOrThrow(be);
        BlockOwnerData.getDataOrThrow(entity).putData(BlockOwnerData.OWNER_KEY, ownerId);
    }

    @Inject(method = "updateControllers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2, shift = At.Shift.AFTER))
    public void sdm$updateControllersSecond(BlockState state, Level world, BlockPos pos, Direction side, CallbackInfo ci){
        final BlockPos controllerPos = pos.relative(side);
        final BlockEntity entity = world.getBlockEntity(controllerPos);

        if(entity == null) return;

        final CrushingWheelBlockEntity be = (CrushingWheelBlockEntity)world.getBlockEntity(pos);

        if(be == null) return;

        final UUID ownerId = BlockOwnerData.getOrThrow(be);
        BlockOwnerData.getDataOrThrow(entity).putData(BlockOwnerData.OWNER_KEY, ownerId);
    }
}
