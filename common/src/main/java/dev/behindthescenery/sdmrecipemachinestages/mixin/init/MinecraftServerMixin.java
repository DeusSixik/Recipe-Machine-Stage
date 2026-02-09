package dev.behindthescenery.sdmrecipemachinestages.mixin.init;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {


    @Inject(method = "reloadResources", at = @At("HEAD"))
    public void bts$reloadResources$before(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        RMSContainer.Instance.isServer = true;
        RMSContainer.Instance.clearData();
    }

    @Inject(method = "reloadResources", at = @At("RETURN"))
    public void bts$reloadResources$after(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        final MinecraftServer server = (MinecraftServer)(Object)this;
        cir.getReturnValue().thenAccept(s -> RMSMain.onServerReloadResources(server));
    }
}
