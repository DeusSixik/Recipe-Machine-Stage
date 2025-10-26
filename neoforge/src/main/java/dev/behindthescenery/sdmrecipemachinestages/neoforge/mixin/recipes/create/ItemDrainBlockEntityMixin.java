package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.utils.RMSCreateUtils;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Deprecated
@Mixin(ItemDrainBlockEntity.class)
public abstract class ItemDrainBlockEntityMixin extends SmartBlockEntity implements IHaveGoggleInformation {

    protected ItemDrainBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = {"tryInsertingFromSide", "tick", "continueProcessing"}, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/transfer/GenericItemEmptying;canItemBeEmptied(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean bts$redirect$canItemBeEmptied(Level i, ItemStack world) {
        return RMSCreateUtils.canItemBeEmptied(i, world, RMSUtils.getBlockOwner(this));
    }
}
