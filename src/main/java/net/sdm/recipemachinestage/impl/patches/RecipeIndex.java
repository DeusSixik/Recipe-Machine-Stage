package net.sdm.recipemachinestage.impl.patches;

import net.minecraft.world.item.crafting.Recipe;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public interface RecipeIndex {

    final class Storage {
        private static final Map<Object, Integer> INDICES = Collections.synchronizedMap(new WeakHashMap<>());

        private Storage() { }

        public static int getIndex(Object value) {
            Integer index = INDICES.get(value);
            return index == null ? -1 : index;
        }

        public static void setIndex(Object value, int index) {
            if (index < 0) {
                INDICES.remove(value);
                return;
            }

            INDICES.put(value, index);
        }
    }

    final class Fallback implements RecipeIndex {

        private final Object internal;

        public Fallback(Object internal) {
            this.internal = internal;
        }

        @Override
        public int gpr$getIndex() {
            return Storage.getIndex(internal);
        }

        @Override
        public void gpr$setIndex(int index) {
            Storage.setIndex(internal, index);
        }
    }

    static RecipeIndex get(Recipe<?> recipe) {
        if (recipe instanceof RecipeIndex index) {
            return index;
        }

        return new Fallback(recipe);
    }

    int gpr$getIndex();

    void gpr$setIndex(int index);
}
