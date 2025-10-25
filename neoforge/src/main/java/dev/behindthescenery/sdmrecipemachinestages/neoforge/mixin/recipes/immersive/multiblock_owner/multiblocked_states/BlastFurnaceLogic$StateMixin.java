package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive.multiblock_owner.multiblocked_states;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.BlastFurnaceLogic;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(BlastFurnaceLogic.State.class)
public class BlastFurnaceLogic$StateMixin implements ImmersiveOwnerProvider {


    @Shadow
    @Final
    FurnaceHandler<BlastFurnaceRecipe> furnace;

    @Override
    public void sdm$setOwner(UUID ownerId) {
        ((ImmersiveOwnerProvider)furnace).sdm$setOwner(ownerId);
    }

    @Override
    public UUID sdm$getOwner() {
       return  ((ImmersiveOwnerProvider)furnace).sdm$getOwner();
    }
}
