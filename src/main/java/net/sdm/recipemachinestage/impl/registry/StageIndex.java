package net.sdm.recipemachinestage.impl.registry;

import it.unimi.dsi.fastutil.objects.*;
import net.sdm.recipemachinestage.utils.RMSUtils;

import java.util.List;

public final class StageIndex {

    private static final Object2ShortMap<String> STAGE_TO_INDEX = new Object2ShortOpenHashMap<>();
    private static final List<String> INDEX_TO_STAGE = new ObjectArrayList<>();

    public static synchronized short getOrCreate(String stageId) {
        return STAGE_TO_INDEX.computeIfAbsent(stageId, id -> {
            int idx = INDEX_TO_STAGE.size();
            INDEX_TO_STAGE.add(stageId);
            return (short) idx;
        });
    }

    public static String getStageId(short index) {
        return INDEX_TO_STAGE.get(index);
    }
}
