package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.overgeared;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
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
     * @param heatedItem the item being ground; may be {@code null} when Overgeared receives an invalid stack.
     * @param level the level containing the active recipe manager; may be {@code null} for an invalid interaction.
     * @param player the player performing the grinding action.
     * @param heldStack the complete held stack supplied to Overgeared's grinding method.
     * @return the accessible recipe output item, the original item when no recipe matches, or {@code null} when the
     * input is invalid or every matching recipe is stage-restricted.
     */
    @Redirect(
            method = "grindItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/stirdrem/overgeared/event/ModItemInteractEvents;getGrindable(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static Item bts$getAccessibleGrindingResult(
            Item heatedItem,
            Level level,
            Player player,
            ItemStack heldStack
    ) {
        if (heatedItem == null || level == null) {
            return null;
        }

        final SingleRecipeInput input = new SingleRecipeInput(new ItemStack(heatedItem));
        boolean hasMatchingRecipe = false;
        for (final var holder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.GRINDING_RECIPE.get())) {
            final GrindingRecipe recipe = holder.value();
            if (!recipe.matches(input, level)) {
                continue;
            }

            hasMatchingRecipe = true;
            if (!RMSUtils.canProcess(player, holder)) {
                continue;
            }

            final ItemStack result = recipe.getResultItem(level.registryAccess());
            return result.isEmpty() ? heatedItem : result.getItem();
        }

        return hasMatchingRecipe ? null : heatedItem;
    }
}
