package net.sdm.recipemachinestage.mixin.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.sdm.recipemachinestage.SupportBlockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void sdm$onPlaceItemIntoWorldPlace(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemstack, Level level, Player player, Item item, int size, CompoundTag nbt, ItemStack copy, InteractionResult ret, int newSize, CompoundTag newNBT, List blockSnapshots, Direction side, boolean eventResult, Iterator var14, BlockSnapshot snap, int updateFlag, BlockState oldBlock, BlockState newBlock){
        SupportBlockData.placerBlock = player;
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At("TAIL"))
    private static void sdm$onPlaceItemIntoWorldPost(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        SupportBlockData.placerBlock = null;
    }
}
