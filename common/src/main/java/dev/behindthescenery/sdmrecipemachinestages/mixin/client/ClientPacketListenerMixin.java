package dev.behindthescenery.sdmrecipemachinestages.mixin.client;

import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void bts$handleLogin$after(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        if(!RMSContainer.Instance.isServer)
            RMSContainer.Instance.clearData();
    }
}
