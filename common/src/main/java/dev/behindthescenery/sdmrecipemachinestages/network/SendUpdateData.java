package dev.behindthescenery.sdmrecipemachinestages.network;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SendUpdateData(boolean nil) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SendUpdateData> TYPE = new Type<>(ResourceLocation.tryBuild(SdmRecipeMachineStages.MOD_ID, "sync_data"));
    public static final StreamCodec<ByteBuf, SendUpdateData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SendUpdateData::nil,
                    SendUpdateData::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendUpdateData packet, NetworkManager.PacketContext context) {
        RMSMain.getListeners().forEach(IRecipeUpdateListener::updateRecipe);
    }

}
