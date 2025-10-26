package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.roots;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mysticmods.roots.api.recipe.IRootsRecipe;
import mysticmods.roots.api.recipe.type.ResolvingRecipeType;
import mysticmods.roots.api.ritual.Ritual;
import mysticmods.roots.blockentity.PyreBlockEntity;
import mysticmods.roots.ritual.SummonCreaturesRitual;
import mysticmods.roots.util.PositionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SummonCreaturesRitual.class)
public abstract class SummonCreaturesRitualMixin extends Ritual {

    @Unique
    private PyreBlockEntity bts$blockEntity;

    @Inject(method = "functionalTick", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;findRecipe(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public void bts$functionalTick(Level pLevel, BlockPos pPos, BlockState pState, PositionCache pCache, PyreBlockEntity blockEntity, int duration, RandomSource randomSource, CallbackInfo ci) {
        this.bts$blockEntity = blockEntity;
    }

    @Redirect(method = "functionalTick", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;findRecipe(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public <V, C extends RecipeInput, T extends Recipe<C> & IRootsRecipe<C>> RecipeHolder<T> bts$revalidateRecipe$redirect(ResolvingRecipeType<V,C,T> instance, C c, Level inventory) {
        final RecipeHolder<T> recipe = instance.findRecipe(c, inventory);
        if(recipe == null) return null;
        return RMSUtils.canProcess(bts$blockEntity, recipe) ? recipe : null;
    }
}
