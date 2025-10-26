package dev.behindthescenery.sdmrecipemachinestages.utils;

import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import dev.behindthescenery.sdmstages.StageApi;

public class RMSStageUtilsClient {

    public static boolean isUnlocked(RecipeBlockType blockType) {
        return StageApi.getClientStage().contains(blockType.stageId());
    }
}
