package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import dev.behindthescenery.sdmrecipemachinestages.api.RMSRecipeIdSupport;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MekanismRecipeType.class)
public abstract class MekanismRecipeTypeMixin<VANILLA_INPUT extends RecipeInput, RECIPE extends MekanismRecipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache> {

    @Shadow
    private List<RecipeHolder<RECIPE>> cachedRecipes;

    @Shadow
    @NotNull
    protected abstract List<RecipeHolder<RECIPE>> getRecipesUncached(@NotNull RecipeManager recipeManager, @Nullable RegistryAccess registryAccess);

    @Shadow
    @Final
    private INPUT_CACHE inputCache;


    @Inject(method = "getRecipes(Lnet/minecraft/world/item/crafting/RecipeManager;Lnet/minecraft/core/RegistryAccess;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;", shift = At.Shift.AFTER))
    public void sdm$getRecipes(@NotNull RecipeManager recipeManager, @Nullable RegistryAccess registryAccess, CallbackInfoReturnable<List<RecipeHolder<RECIPE>>> cir, @Local List<RecipeHolder<RECIPE>> cachedRecipes) {
        for (RecipeHolder<RECIPE> cachedRecipe : cachedRecipes) {
            final var recipe = cachedRecipe.value();
            if(recipe instanceof RMSRecipeIdSupport support)
                support.rms$setRecipeId(cachedRecipe.id());
        }
    }
}
