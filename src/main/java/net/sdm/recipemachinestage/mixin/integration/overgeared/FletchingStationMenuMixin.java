package net.sdm.recipemachinestage.mixin.integration.overgeared;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.compat.overgeared.OvergearedRecipeSupport;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.stirdrem.overgeared.screen.FletchingStationMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/**
 * Applies recipe-stage restrictions to Overgeared fletching station menus.
 */
@Mixin(FletchingStationMenu.class)
public abstract class FletchingStationMenuMixin {

    /**
     * Stores the player whose fletching menu performs recipe lookups.
     */
    @Shadow(remap = false)
    @Final
    private Player player;

    /**
     * Filters every fletching lookup used for preview, input consumption, and pickup validation.
     *
     * @param recipeManager the menu's recipe manager.
     * @param recipeType the fletching recipe type being queried.
     * @param container the current fletching station input.
     * @param level the level used for recipe matching.
     * @param <C> the recipe container type.
     * @param <T> the recipe value type.
     * @return the matching recipe when the menu player has access, otherwise an empty optional.
     */
    @Redirect(
            method = {"updateResultSlot", "consumeInputs", "canStillCraft"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private <C extends Container, T extends Recipe<C>> Optional<T> recipeMachineStage$filterFletchingRecipe(
            RecipeManager recipeManager,
            RecipeType<T> recipeType,
            C container,
            Level level
    ) {
        return OvergearedRecipeSupport.filter(
                recipeManager.getRecipeFor(recipeType, container, level),
                recipe -> RMSUtils.canRecipe(recipe, player)
        );
    }
}
