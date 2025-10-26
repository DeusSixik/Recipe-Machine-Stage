package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.roots;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mysticmods.roots.api.recipe.IRootsRecipe;
import mysticmods.roots.api.recipe.type.ResolvingRecipeType;
import mysticmods.roots.blockentity.FungalTransmuterBlockEntity;
import mysticmods.roots.blockentity.template.UseDelegatedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FungalTransmuterBlockEntity.class)
public abstract class FungalTransmuterBlockEntityMixin extends UseDelegatedBlockEntity {

    protected FungalTransmuterBlockEntityMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Redirect(method = "revalidateRecipe", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;findRecipe(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public <V, C extends RecipeInput, T extends Recipe<C> & IRootsRecipe<C>> RecipeHolder<T> bts$revalidateRecipe$redirect(ResolvingRecipeType<V,C,T> instance, C c, Level inventory) {
        final RecipeHolder<T> recipe = instance.findRecipe(c, inventory);
        if(recipe == null) return null;
        return RMSUtils.ifCanProcessReturnRecipeOrNull(this, recipe);
    }

    @Redirect(method = "revalidateRecipe", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public <V, C extends RecipeInput, T extends Recipe<C> & IRootsRecipe<C>> RecipeHolder<T> bts$revalidateRecipe(ResolvingRecipeType<V,C,T> instance, Level level, ResourceLocation location) {
        final RecipeHolder<T> recipe = instance.getRecipe(level, location);
        if(recipe == null) return null;
        return RMSUtils.ifCanProcessReturnRecipeOrNull(this, recipe);
    }
}
