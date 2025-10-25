package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IMultiblockState.class)
public interface IMultiblockStateMixin extends ImmersiveOwnerProvider {
}
