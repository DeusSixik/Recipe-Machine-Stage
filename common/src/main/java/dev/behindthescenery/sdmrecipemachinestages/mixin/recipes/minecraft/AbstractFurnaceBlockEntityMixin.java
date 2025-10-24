package dev.behindthescenery.sdmrecipemachinestages.mixin.recipes.minecraft;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getMaxStackSize()I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sdm$serverTick(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfo ci, boolean bl, boolean bl2, ItemStack itemStack, ItemStack itemStack2, boolean bl3, boolean bl4, RecipeHolder recipeHolder){
        if(!RMSUtils.canProcess(abstractFurnaceBlockEntity, recipeHolder))
            ci.cancel();
    }
}
