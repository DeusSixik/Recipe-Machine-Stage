package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.immersive;

import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.FurnaceHandler;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.immersive.ImmersiveOwnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FurnaceHandler.class)
public class FurnaceHandlerMixin<R extends IESerializableRecipe> implements ImmersiveOwnerProvider {

    @Unique
    private UUID sdm$owner;

    @Override
    public void sdm$setOwner(UUID ownerId) {
        this.sdm$owner = ownerId;
    }

    @Override
    public UUID sdm$getOwner() {
        return sdm$owner;
    }

    @Inject(method = "getRecipe", at = @At("HEAD"))
    public void bts$get(FurnaceHandler.IFurnaceEnvironment<R> env, CallbackInfoReturnable<R> cir) {


    }
}
