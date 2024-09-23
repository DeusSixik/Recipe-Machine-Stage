package net.sdm.recipemachinestage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerableSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "onPlace", at = @At("HEAD"))
    public void sdm$onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_, CallbackInfo ci){
        if(!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IOwnerableSupport support) {
                if(SupportBlockData.placerBlock != null) {
                    support.recipe_machine_stage$setOwner(SupportBlockData.placerBlock.getGameProfile().getId());
                }
            }
        }
    }
}
