package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.overgeared;

import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.overgeared.OvergearedRecipeSupport;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.stirdrem.overgeared.screen.RockKnappingMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/**
 * Applies recipe-stage restrictions to Overgeared rock knapping menus.
 */
@Mixin(RockKnappingMenu.class)
public abstract class RockKnappingMenuMixin {

    /**
     * Stores the player whose knapping grid performs recipe lookups.
     */
    @Shadow
    @Final
    private Player player;

    /**
     * Filters the knapping result lookup according to the current player's stages.
     *
     * @param recipeManager the Overgeared menu's recipe manager.
     * @param recipeType the recipe type being queried.
     * @param recipeInput the current knapping grid input.
     * @param level the level used to evaluate recipe matching.
     * @param <I> the recipe input type.
     * @param <T> the recipe type associated with the input.
     * @return the matching recipe when the menu player has access, otherwise an empty optional.
     */
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> bts$filterKnappingRecipe(
            RecipeManager recipeManager,
            RecipeType<T> recipeType,
            I recipeInput,
            Level level
    ) {
        return OvergearedRecipeSupport.filter(
                recipeManager.getRecipeFor(recipeType, recipeInput, level),
                holder -> RMSUtils.canProcess(player, holder)
        );
    }
}
