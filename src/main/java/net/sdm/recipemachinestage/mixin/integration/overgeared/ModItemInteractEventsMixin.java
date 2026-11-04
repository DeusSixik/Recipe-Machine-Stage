package net.sdm.recipemachinestage.mixin.integration.overgeared;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sdm.recipemachinestage.utils.RMSUtils;
import net.stirdrem.overgeared.event.ModItemInteractEvents;
import net.stirdrem.overgeared.recipe.GrindingRecipe;
import net.stirdrem.overgeared.recipe.ModRecipeTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Applies recipe-stage restrictions to Overgeared's player-driven grinding action.
 */
@Mixin(ModItemInteractEvents.class)
public abstract class ModItemInteractEventsMixin {

    /**
     * Resolves a grinding result from only the recipes available to the interacting player.
     *
     * @param inputItem the item being ground; may be {@code null} when Overgeared receives invalid input.
     * @param level the level containing the active recipe manager; may be {@code null} for invalid input.
     * @param player the player performing the grinding action.
     * @param heldStack the complete held stack supplied to Overgeared's grinding method.
     * @return the accessible recipe output item, the original item when no recipe matches, or {@code null} when the
     * input is invalid or every matching recipe is stage-restricted.
     */
    @Redirect(
            method = "grindItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/stirdrem/overgeared/event/ModItemInteractEvents;getGrindable(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/Item;",
                    remap = false
            ),
            remap = false
    )
    private static Item recipeMachineStage$getAccessibleGrindingResult(
            Item inputItem,
            Level level,
            Player player,
            ItemStack heldStack
    ) {
        if (inputItem == null || level == null) {
            return null;
        }

        final SimpleContainer input = new SimpleContainer(new ItemStack(inputItem));
        boolean hasMatchingRecipe = false;
        for (final GrindingRecipe recipe : level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.GRINDING_RECIPE.get())) {
            if (!recipe.matches(input, level)) {
                continue;
            }

            hasMatchingRecipe = true;
            if (!RMSUtils.canRecipe(recipe, player)) {
                continue;
            }

            final ItemStack result = recipe.getResultItem(level.registryAccess());
            return result.isEmpty() ? inputItem : result.getItem();
        }

        return hasMatchingRecipe ? null : inputItem;
    }
}
