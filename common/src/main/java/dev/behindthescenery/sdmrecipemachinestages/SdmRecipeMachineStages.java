package dev.behindthescenery.sdmrecipemachinestages;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.behindthescenery.sdmrecipemachinestages.supported.RMSSupportedTypes;
import dev.behindthescenery.sdmstages.events.StagesEvents;

public final class SdmRecipeMachineStages {
    public static final String MOD_ID = "sdmrecipemachinestages";

    public static void init() {
        RMSSupportedTypes.init();

        LifecycleEvent.SERVER_STARTED.register(RMSMain::onServerStarted);
        StagesEvents.ON_STAGE_SYNC.register(RMSMain::onStageSync);
    }
}
