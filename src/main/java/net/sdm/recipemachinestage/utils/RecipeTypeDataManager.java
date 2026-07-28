package net.sdm.recipemachinestage.utils;

import net.minecraft.world.item.crafting.RecipeType;

import java.util.IdentityHashMap;
import java.util.Map;

public class RecipeTypeDataManager {
    private static final Map<RecipeType<?>, RecipeTypeData> DATA_MAP = new IdentityHashMap<>();

    public static RecipeTypeData getData(RecipeType<?> type) {
        return DATA_MAP.computeIfAbsent(type, k -> new RecipeTypeData());
    }

    public static class RecipeTypeData {
        public boolean hasRestrictions = false;
        public int index = -1;
    }
}
