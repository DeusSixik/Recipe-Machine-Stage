package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FurnaceHandler.IFurnaceEnvironment.class)
public interface IFurnaceEnvironmentMixin extends ImmersiveOwnerProvider {

}
