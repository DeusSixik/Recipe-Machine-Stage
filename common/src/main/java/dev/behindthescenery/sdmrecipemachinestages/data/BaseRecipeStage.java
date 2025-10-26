package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.server.level.ServerPlayer;

public interface BaseRecipeStage {

    default boolean hasStage(ServerPlayer serverPlayer) {
        final Stage stage = RMSMain.getStageContainer().getStage(serverPlayer);
        if(stage == null) return false;
        return stage.contains(stageId());
    }

    String stageId();

}
