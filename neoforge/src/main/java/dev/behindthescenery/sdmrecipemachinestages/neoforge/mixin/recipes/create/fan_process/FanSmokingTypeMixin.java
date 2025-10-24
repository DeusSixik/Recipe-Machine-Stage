package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.fan_process;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.FanProcessingTypePatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(AllFanProcessingTypes.SmokingType.class)
public abstract class FanSmokingTypeMixin implements FanProcessingTypePatch {

    @Override
    public @Nullable List<ItemStack> bts$process(ItemStack stack, Level level, UUID ownerId) {
        final Optional<RecipeHolder<SmokingRecipe>> smokingRecipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMOKING, new SingleRecipeInput(stack), level)
                .filter(AllRecipeTypes.CAN_BE_AUTOMATED);

        if (smokingRecipe.isPresent()) {
           final RecipeHolder<SmokingRecipe> getting = smokingRecipe.get();
           return RMSUtils.canProcess(ownerId, getting) ? RecipeApplier.applyRecipeOn(level, stack, smokingRecipe.get()) : null;
        }


        return null;
    }
}
