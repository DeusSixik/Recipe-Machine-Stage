package dev.behindthescenery.sdmrecipemachinestages;

import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.network.SyncRMSContainerDataS2C;
import dev.behindthescenery.sdmrecipemachinestages.network.SyncRecipesAndStagesS2C;
import dev.behindthescenery.sdmrecipemachinestages.supported.RMSSupportedTypes;
import dev.behindthescenery.sdmstages.events.StagesEvents;

public final class SdmRecipeMachineStages {
    public static final String MOD_ID = "sdmrecipemachinestages";

    public static void init() {
        RMSSupportedTypes.init();
        LifecycleEvent.SERVER_STARTED.register(RMSMain::onServerStarted);
        StagesEvents.ON_STAGE_SYNC.register(RMSMain::onStageSync);
        BlockEvent.PLACE.register(RMSMain::onPlaceBlock);
        PlayerEvent.PLAYER_JOIN.register(RMSMain::onPlayerJoin);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncRecipesAndStagesS2C.TYPE, SyncRecipesAndStagesS2C.STREAM_CODEC, SyncRecipesAndStagesS2C::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncRMSContainerDataS2C.TYPE, SyncRMSContainerDataS2C.STREAM_CODEC, SyncRMSContainerDataS2C::handle);
    }
}
