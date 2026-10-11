package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin extends KineticBlockEntity {

    @Shadow
    protected abstract Optional<BasinBlockEntity> getBasin();

    @Shadow
    protected abstract Object getRecipeCacheKey();

    @Shadow
    protected abstract boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe);

    @Shadow
    protected abstract <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe);

    protected BasinOperatingBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author Sixik
     * @reason Change logic
     */
    @Inject(method = "getMatchingRecipes", at = @At("HEAD"), cancellable = true)
    protected void getMatchingRecipes(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (getBasin().map(BasinBlockEntity::isEmpty)
                .orElse(true)) {
            cir.setReturnValue(new ArrayList<>());
            return;
        }

        final List<Recipe<?>> list = new ArrayList<>();

        RMSUtils.filterRecipes(RecipeFinder.get(
                getRecipeCacheKey(), level, this::matchStaticFilters), RMSUtils.getBlockOwner(this),
                s -> matchBasinRecipe(s.value()))
                .forEach(s -> list.add(s.value()));

        list.sort((r1, r2) -> r2.getIngredients().size() - r1.getIngredients().size());

        cir.setReturnValue(list);
    }
}
