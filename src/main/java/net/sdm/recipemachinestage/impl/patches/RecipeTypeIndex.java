package net.sdm.recipemachinestage.impl.patches;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public interface RecipeTypeIndex {

    static RecipeTypeIndex get(RecipeType<?> type) {
        if(type instanceof RecipeTypeIndex)
            return (RecipeTypeIndex) type;
        return new Fallback(type);
    }

    int gpr$getIndex();

    void gpr$setIndex(int index);

    final class Fallback implements RecipeTypeIndex {

        private final RecipeType<?> internal;

        public Fallback(RecipeType<?> internal) {
            this.internal = internal;
        }

        @Override
        public int gpr$getIndex() {
            return BuiltInRegistries.RECIPE_TYPE.getId(internal);
        }

        @Override
        public void gpr$setIndex(int index) { }
    }
}
