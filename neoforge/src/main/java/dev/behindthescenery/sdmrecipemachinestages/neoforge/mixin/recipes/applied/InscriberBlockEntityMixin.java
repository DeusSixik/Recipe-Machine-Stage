package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.applied;

import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberRecipe;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.custom_data.BlockOwnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InscriberBlockEntity.class)
public abstract class InscriberBlockEntityMixin extends AENetworkedPoweredBlockEntity {

    public InscriberBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "getTask", at = @At("HEAD"))
    public void bts$getTask(CallbackInfoReturnable<InscriberRecipe> cir) {
        RMSMain.setBlockOwner(BlockOwnerData.getOrThrow(this));
    }
}
