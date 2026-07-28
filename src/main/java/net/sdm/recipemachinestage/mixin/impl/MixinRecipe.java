package net.sdm.recipemachinestage.mixin.impl;


import net.minecraft.world.item.crafting.Recipe;
import net.sdm.recipemachinestage.impl.patches.RecipeIndex;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Recipe.class)
public interface MixinRecipe extends RecipeIndex {

    @Override
    default int gpr$getIndex() {
        return RecipeIndex.Storage.getIndex(this);
    }

    @Override
    default void gpr$setIndex(int index) {
        RecipeIndex.Storage.setIndex(this, index);
    }
}
