package net.sdm.recipemachinestage.mixin.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeIndex;
import net.sdm.recipemachinestage.impl.patches.RecipeTypeSupport;
import net.sdm.recipemachinestage.utils.FastRecipeTypeCache;
import net.sdm.recipemachinestage.utils.RecipeTypeDataManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeType.class)
public interface MixinRecipeType extends RecipeTypeSupport, RecipeTypeIndex {

    @Override
    default boolean gpr$hasRestrictions() {
        return RecipeTypeDataManager.getData((RecipeType<?>) this).hasRestrictions;
    }

    @Override
    default void gpr$setHasRestrictions(boolean hasRestrictions) {
        RecipeTypeDataManager.getData((RecipeType<?>) this).hasRestrictions = hasRestrictions;
    }

    @Override
    default int gpr$getIndex() {
        RecipeTypeDataManager.RecipeTypeData data = RecipeTypeDataManager.getData((RecipeType<?>) this);

        if (data.index == -1) {
            FastRecipeTypeCache.init();
            if (data.index == -1) {
                data.index = BuiltInRegistries.RECIPE_TYPE.getId((RecipeType<?>) this);
            }
        }
        return data.index;
    }

    @Override
    default void gpr$setIndex(int index) {
        RecipeTypeDataManager.getData((RecipeType<?>) this).index = index;
    }
}
