package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create.fan_process;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.create.FanProcessingTypePatch;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(AllFanProcessingTypes.HauntingType.class)
public abstract class FanHauntingTypeMixin implements FanProcessingTypePatch {


    @Override
    public @Nullable List<ItemStack> bts$process(ItemStack stack, Level level, UUID ownerId) {
        final Optional<RecipeHolder<Recipe<SingleRecipeInput>>> recipe = AllRecipeTypes.HAUNTING.find(new SingleRecipeInput(stack), level);
        if (recipe.isPresent()) {
            final RecipeHolder<Recipe<SingleRecipeInput>> getting = recipe.get();
            return RMSUtils.canProcess(ownerId, getting) ? RecipeApplier.applyRecipeOn(level, stack, recipe.get()) : null;
        }
        return null;
    }
}
