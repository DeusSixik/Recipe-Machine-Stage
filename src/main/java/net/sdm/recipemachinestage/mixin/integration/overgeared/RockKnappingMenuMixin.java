package net.sdm.recipemachinestage.mixin.integration.overgeared;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.compat.overgeared.OvergearedRecipeSupport;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.stirdrem.overgeared.screen.RockKnappingMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * Applies recipe-stage restrictions to Overgeared rock knapping menus.
 */
@Mixin(RockKnappingMenu.class)
public abstract class RockKnappingMenuMixin {

    /**
     * Stores the player whose knapping grid performs recipe lookups.
     */
    @Shadow(remap = false)
    @Final
    private Player player;

    /**
     * Filters the knapping recipes before Overgeared selects the first matching pattern.
     *
     * @param recipeManager the menu's recipe manager.
     * @param recipeType the knapping recipe type being queried.
     * @param <C> the recipe container type.
     * @param <T> the recipe value type.
     * @return a new list containing only recipes accessible to the menu player.
     */
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;"
            )
    )
    private <C extends Container, T extends Recipe<C>> List<T> recipeMachineStage$filterKnappingRecipes(
            RecipeManager recipeManager,
            RecipeType<T> recipeType
    ) {
        return OvergearedRecipeSupport.filter(
                recipeManager.getAllRecipesFor(recipeType),
                recipe -> RMSUtils.canRecipe(recipe, player)
        );
    }
}
