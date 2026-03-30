package dev.behindthescenery.sdmrecipemachinestages.mixin.init;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.api.RMSApi;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "reloadResources", at = @At("HEAD"))
    public void bts$reloadResources$before(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        RMSMain.onServerReloadResources((MinecraftServer)(Object) this, false);
    }

    @Redirect(method = "reloadResources", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenAcceptAsync(Ljava/util/function/Consumer;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    public <T> CompletableFuture<Void> bts$reloadResources$thenAcceptAsync(CompletableFuture<T> instance, Consumer<? super T> action, Executor executor) {
        return instance.thenAcceptAsync(action, executor).thenAcceptAsync((s) -> {
            RMSApi.syncRecipeContainerWithPlayers();
        }, executor);
    }


//    @Inject(method = "reloadResources", at = @At("RETURN"))
//    public void bts$reloadResources$after(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
//        final MinecraftServer server = (MinecraftServer)(Object)this;
//
//        cir.getReturnValue().thenApply(s -> {
//            RMSMain.onServerReloadResources(server, true);
//            return null;
//        });
//    }
}
