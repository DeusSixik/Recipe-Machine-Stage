package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.fan_process;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.FanProcessingTypePatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(AllFanProcessingTypes.BlastingType.class)
public abstract class FanBlastingTypeMixin implements FanProcessingTypePatch {

    @Override
    public @Nullable List<ItemStack> bts$process(ItemStack stack, Level level, UUID ownerId) {
        final Optional<RecipeHolder<SmokingRecipe>> smokingRecipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMOKING, new SingleRecipeInput(stack), level)
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED);

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> smeltingRecipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level)
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED);

        if (smeltingRecipe.isEmpty()) {
            smeltingRecipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(stack), level);
        }

        if (smeltingRecipe.isPresent()) {
            RegistryAccess registryAccess = level.registryAccess();
            if (smokingRecipe.isEmpty() || !ItemStack.isSameItem(smokingRecipe.get().value()
                            .getResultItem(registryAccess),
                    smeltingRecipe.get().value()
                            .getResultItem(registryAccess))) {

                final RecipeHolder<? extends AbstractCookingRecipe> getting = smeltingRecipe.get();
                return RMSUtils.canProcess(ownerId, getting) ? RecipeApplier.applyRecipeOn(level, stack, getting) : Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }
}
