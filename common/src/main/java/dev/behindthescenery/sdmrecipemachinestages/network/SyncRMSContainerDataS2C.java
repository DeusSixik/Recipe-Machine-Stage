package dev.behindthescenery.sdmrecipemachinestages.network;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.compat.IRecipeUpdateListener;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncRMSContainerDataS2C(RecipeBlockType recipeBlockType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncRMSContainerDataS2C> TYPE = new Type<>(ResourceLocation.tryBuild(SdmRecipeMachineStages.MOD_ID, "sync_rms_container_s2c"));

    public static final StreamCodec<ByteBuf, SyncRMSContainerDataS2C> STREAM_CODEC = StreamCodec.composite(
            RecipeBlockType.STREAM_CODEC, SyncRMSContainerDataS2C::recipeBlockType, SyncRMSContainerDataS2C::new
    );

    public static void handle(SyncRMSContainerDataS2C packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if(!RMSContainer.Instance.isServer) {
                RMSContainer.Instance.register(packet.recipeBlockType);
            }

            RMSMain.getListeners().forEach(IRecipeUpdateListener::updateRecipe);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
