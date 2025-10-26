package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.roots;

import com.llamalad7.mixinextras.sugar.Local;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import mysticmods.roots.api.recipe.IRootsRecipe;
import mysticmods.roots.api.recipe.type.ResolvingRecipeType;
import mysticmods.roots.item.RunicShearsItem;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RunicShearsItem.RunicShearsDispenseBehaviour.class)
public abstract class RunicShearsDispenseBehaviourMixin extends DefaultDispenseItemBehavior {

    @Unique
    private Player bts$player;

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;findRecipe(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public void bts$useOn(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<ItemStack> cir, @Local Player fakePlayer) {
        bts$player = fakePlayer;
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lmysticmods/roots/api/recipe/type/ResolvingRecipeType;findRecipe(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    public <V, C extends RecipeInput, T extends Recipe<C> & IRootsRecipe<C>> RecipeHolder<T> bts$revalidateRecipe$redirect(ResolvingRecipeType<V,C,T> instance, C c, Level inventory) {
        final RecipeHolder<T> recipe = instance.findRecipe(c, inventory);
        if(recipe == null) return null;
        return RMSUtils.canProcess(RMSUtils.getPlayerId(bts$player), recipe) ? recipe : null;
    }
}
