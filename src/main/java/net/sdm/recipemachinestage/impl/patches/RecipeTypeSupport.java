package net.sdm.recipemachinestage.impl.patches;

import net.minecraft.world.item.crafting.RecipeType;

public interface RecipeTypeSupport {

    final class Fallback implements RecipeTypeSupport {
        public static final Fallback INSTANCE = new Fallback();

        @Override
        public boolean gpr$hasRestrictions() {
            return false;
        }

        @Override
        public void gpr$setHasRestrictions(boolean hasRestrictions) { }
    }

    static RecipeTypeSupport get(RecipeType<?> type) {
        if(type instanceof RecipeTypeSupport)
            return (RecipeTypeSupport) type;

        return Fallback.INSTANCE;
    }

    boolean gpr$hasRestrictions();

    void gpr$setHasRestrictions(boolean hasRestrictions);
}
